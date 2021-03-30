package com.exasol.bucketfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class SyncAwareBucket extends WriteEnabledBucket implements Bucket {
    private static final long BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS = 60000;
    private static final long FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS = 200;
    private final BucketFsMonitor monitor;

    protected SyncAwareBucket(final Builder<? extends Builder<?>> builder) {
        super(builder);
        this.monitor = builder.monitor;
    }

    // [impl->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Override
    public boolean isObjectSynchronized(final String pathInBucket, final Instant afterUTC)
            throws InterruptedException, BucketAccessException {
        return this.monitor.isObjectSynchronized(this, pathInBucket, afterUTC);
    }

    // [impl->dsn~uploading-to-bucket~1]
    @Override
    public void uploadFile(final Path localPath, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadFile(localPath, pathInBucket, true);
    }

    // [impl->dsn~uploading-to-bucket~1]
    @Override
    public void uploadFile(final Path localPath, final String pathInBucket, final boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final String extendedPathInBucket = extendPathInBucketDownToFilename(localPath, pathInBucket);
        try {
            uploadContent(BodyPublishers.ofFile(localPath), extendedPathInBucket, "file " + extendedPathInBucket,
                    blocking);
        } catch (final IOException exception) {
            throw new BucketAccessException("I/O failed to open file \"" + localPath + "\" for upload to BucketFS.",
                    exception);
        }
    }

    // Wait some time between uploads of the same file so we can distinguish the upload success logs for detecting
    // upload success.
    // [impl->dsn~bucketfs-object-overwrite-throttle~1]
    private void delayRepeatedUploadToSamePath(final String extendedPathInBucket) throws InterruptedException {
        if (this.uploadHistory.containsKey(extendedPathInBucket)) {
            final Instant lastUploadAt = this.uploadHistory.get(extendedPathInBucket).with(ChronoField.NANO_OF_SECOND,
                    0);
            final Instant now = Instant.now();
            if (!now.isAfter(lastUploadAt.plusSeconds(1))) {
                final long delayInMillis = 1000L - (now.getNano() / 1000000L);
                LOGGER.fine(() -> "Delaying upload to \"" + extendedPathInBucket + "\" for " + delayInMillis + " ms");
                Thread.sleep(delayInMillis);
            }
        } else {
            LOGGER.fine(() -> "No previous uploads to \"" + extendedPathInBucket
                    + "\" recorded in upload history. No upload delay required.");
        }
    }

    private void uploadContent(final BodyPublisher bodyPublisher, final String pathInBucket,
            final String contentDescription, final boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException {
        if (blocking) {
            delayRepeatedUploadToSamePath(pathInBucket);
        }
        final long millisSinceEpochBeforeUpload = System.currentTimeMillis();
        uploadContentNonBlocking(bodyPublisher, pathInBucket, contentDescription);
        if (blocking) {
            waitForFileToBeSynchronized(pathInBucket, millisSinceEpochBeforeUpload);
        }
    }

    // [impl->dsn~uploading-strings-to-bucket~1]
    @Override
    public void uploadStringContent(final String content, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadStringContent(content, pathInBucket, true);
    }

    @Override
    public void uploadStringContent(final String content, final String pathInBucket, final boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final String excerpt = (content.length() > 20) ? content.substring(0, 20) + "..." : content;
        final String description = "text " + excerpt;
        uploadContent(BodyPublishers.ofString(content), pathInBucket, description, blocking);
    }

    // [impl->dsn~uploading-input-stream-to-bucket~1]
    @Override
    public void uploadInputStream(final Supplier<InputStream> inputStreamSupplier, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        uploadInputStream(inputStreamSupplier, pathInBucket, true);
    }

    // [impl->dsn~dsn~uploading-input-stream-to-bucket~1]
    @Override
    public void uploadInputStream(final Supplier<InputStream> inputStreamSupplier, final String pathInBucket,
            final boolean blocking) throws InterruptedException, BucketAccessException, TimeoutException {
        uploadContentNonBlocking(BodyPublishers.ofInputStream(inputStreamSupplier), pathInBucket, "input stream");
    }

    // [impl->dsn~waiting-until-archive-extracted~1]
    // [impl->dsn~waiting-until-file-appears-in-target-directory~1]
    private void waitForFileToBeSynchronized(final String pathInBucket, final long millisSinceEpochBeforeUpload)
            throws InterruptedException, TimeoutException, BucketAccessException {
        final long expiry = millisSinceEpochBeforeUpload + BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS;
        final Instant afterUtc = Instant.ofEpochMilli(millisSinceEpochBeforeUpload);
        while (System.currentTimeMillis() < expiry) {
            if (this.monitor.isObjectSynchronized(this, pathInBucket, afterUtc)) {
                return;
            }
            Thread.sleep(FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS);
        }
        final String message = "Timeout waiting for object \"" + pathInBucket + "\" to be synchronized in bucket \""
                + getFullyQualifiedBucketName() + "\" after " + afterUtc + ".";
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

        @SuppressWarnings("unchecked")
        @Override
        protected T self() {
            return (T) this;
        }

        /**
         * Set monitor for this bucket.
         *
         * @param monitor synchronization monitor
         * @return Builder instance for fluent programming
         */
        public T monitor(final BucketFsMonitor monitor) {
            this.monitor = monitor;
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