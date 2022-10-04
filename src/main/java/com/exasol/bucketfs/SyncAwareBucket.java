package com.exasol.bucketfs;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.BucketFsMonitor.State;
import com.exasol.bucketfs.monitor.BucketFsMonitor.StateRetriever;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class SyncAwareBucket extends WriteEnabledBucket implements Bucket {
    private static final Logger LOGGER = Logger.getLogger(SyncAwareBucket.class.getName());
    private static final long BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS = 2 * 60000L;
    private static final long FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS = 200;
    private final BucketFsMonitor monitor;
    private final StateRetriever stateRetriever;

    /**
     * Sync aware bucket.
     *
     * @param builder builder
     */
    protected SyncAwareBucket(final Builder<? extends Builder<?>> builder) {
        super(builder);
        Objects.requireNonNull(builder.monitor);
        Objects.requireNonNull(builder.stateRetriever);
        this.monitor = builder.monitor;
        this.stateRetriever = builder.stateRetriever;
    }

    // [impl->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Override
    public boolean isObjectSynchronized(final String pathInBucket, final BucketFsMonitor.State state)
            throws BucketAccessException {
        return this.monitor.isObjectSynchronized(this, pathInBucket, state);
    }

    // [impl->dsn~uploading-to-bucket~1]
    @Override
    public void uploadFile(final Path localPath, final String pathInBucket)
            throws TimeoutException, BucketAccessException, FileNotFoundException {
        delayRepeatedUploadToSamePath(pathInBucket);
        final BucketFsMonitor.State state = this.stateRetriever.getState();
        final UploadResult uploadResult = uploadFileNonBlocking(localPath, pathInBucket);
        if (uploadResult.wasUploadNecessary()) {
            waitForFileToBeSynchronized(pathInBucket, state);
            recordUploadInHistory(pathInBucket);
        }
    }

    // Wait some time between uploads of the same file so we can distinguish the upload success logs for detecting
    // upload success.
    // [impl->dsn~bucketfs-object-overwrite-throttle~1]
    private void delayRepeatedUploadToSamePath(final String extendedPathInBucket) throws BucketAccessException {
        if (this.uploadHistory.containsKey(extendedPathInBucket)) {
            final Instant lastUploadAt = this.uploadHistory.get(extendedPathInBucket) //
                    .with(ChronoField.NANO_OF_SECOND, 0);
            final Instant now = Instant.now();
            if (now.isAfter(lastUploadAt.plusSeconds(1))) {
                LOGGER.fine(() -> "Last upload to '" + extendedPathInBucket + "' was at " + lastUploadAt
                        + ". No need to add extra delay.");
            } else {
                final long delayInMillis = 1000L - (now.getNano() / 1000000L);
                LOGGER.fine(() -> "Delaying upload to '" + extendedPathInBucket + "' for " + delayInMillis + " ms");
                try {
                    Thread.sleep(delayInMillis);
                } catch (final InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new BucketAccessException(messageBuilder("E-BFSJ-8")
                            .message("Interrupted while delaying repeated upload to {{path}}", extendedPathInBucket)
                            .toString());
                }
            }
        } else {
            LOGGER.fine(() -> "No previous uploads to '" + extendedPathInBucket
                    + "' recorded in upload history. No upload delay required.");
        }
    }

    // [impl->dsn~uploading-strings-to-bucket~1]
    @Override
    public void uploadStringContent(final String content, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        delayRepeatedUploadToSamePath(pathInBucket);
        final BucketFsMonitor.State state = this.stateRetriever.getState();
        uploadStringContentNonBlocking(content, pathInBucket);
        waitForFileToBeSynchronized(pathInBucket, state);
        recordUploadInHistory(pathInBucket);
    }

    // [impl->dsn~uploading-input-stream-to-bucket~1]
    @Override
    public void uploadInputStream(final Supplier<InputStream> inputStreamSupplier, final String pathInBucket)
            throws BucketAccessException, TimeoutException {
        delayRepeatedUploadToSamePath(pathInBucket);
        final BucketFsMonitor.State state = this.stateRetriever.getState();
        uploadInputStreamNonBlocking(inputStreamSupplier, pathInBucket);
        waitForFileToBeSynchronized(pathInBucket, state);
        recordUploadInHistory(pathInBucket);
    }

    // [impl->dsn~waiting-until-archive-extracted~1]
    // [impl->dsn~waiting-until-file-appears-in-target-directory~1]
    private void waitForFileToBeSynchronized(final String pathInBucket, final BucketFsMonitor.State state)
            throws TimeoutException, BucketAccessException {
        final var expiry = System.currentTimeMillis() + BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS;
        while (System.currentTimeMillis() < expiry) {
            if (this.monitor.isObjectSynchronized(this, pathInBucket, state)) {
                return;
            }
            try {
                Thread.sleep(FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new BucketAccessException(messageBuilder("E-BFSJ-10")
                        .message("Interrupted while waiting for {{path}} to be synchronized on BucketFS.", pathInBucket)
                        .toString());
            }
        }
        final String message = String.format(
                "Timeout waiting for object '%s' to be synchronized in bucket '%s' after %s.", //
                pathInBucket, getFullyQualifiedBucketName(), state.toString());
        LOGGER.severe(() -> message);
        throw new TimeoutException(message);
    }

    /**
     * Create builder for a {@link SyncAwareBucket}.
     *
     * @return builder
     */
    @SuppressWarnings("squid:S1452")
    public static Builder<? extends Builder<?>> builder() {
        return new Builder<>();
    }

    /**
     * Builder for {@link SyncAwareBucket} objects.
     *
     * @param <T> type for self pointer to inheritable builder
     */
    public static class Builder<T extends Builder<T>> extends WriteEnabledBucket.Builder<Builder<T>> {
        private BucketFsMonitor monitor;
        private StateRetriever stateRetriever;

        @SuppressWarnings("unchecked")
        @Override
        protected T self() {
            return (T) this;
        }

        /**
         * Set monitor for this bucket.
         *
         * @param value synchronization monitor
         * @return Builder instance for fluent programming
         */
        public T monitor(final BucketFsMonitor value) {
            this.monitor = value;
            return self();
        }

        /**
         * Set state retriever for this bucket. The bucket uses the state retriever to inquire the current {@link State}
         * as observed by the monitor. The bucket can pass the state to the monitor to make the monitor accept only
         * events that happened after the state.
         *
         * @param value state retriever
         * @return Builder instance for fluent programming
         */
        public T stateRetriever(final StateRetriever value) {
            this.stateRetriever = value;
            return self();
        }

        /**
         * Build a new {@link SyncAwareBucket} instance.
         *
         * @return bucket instance
         */
        @Override
        public SyncAwareBucket build() {
            return new SyncAwareBucket(this);
        }
    }
}