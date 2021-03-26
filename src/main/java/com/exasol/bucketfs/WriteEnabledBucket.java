package com.exasol.bucketfs;

import static com.exasol.containers.ExasolContainerConstants.SUPPORTED_ARCHIVE_EXTENSIONS;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Base64;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class WriteEnabledBucket extends ReadEnabledBucket implements Bucket {
    private static final long BUCKET_SYNC_TIMEOUT_IN_MILLISECONDS = 60000;
    private static final long FILE_SYNC_POLLING_DELAY_IN_MILLISECONDS = 200;
    private final String writePassword;
    private final BucketFsMonitor monitor;

    private WriteEnabledBucket(final Builder builder) {
        super(builder);
        this.writePassword = builder.writePassword;
        this.monitor = builder.monitor;
    }

    @Override
    public String getWritePassword() {
        return this.writePassword;
    }

    // [impl->dsn~validating-bucketfs-object-synchronization-via-the-bucketfs-log~1]
    @Override
    public boolean isObjectSynchronized(final String pathInBucket, final Instant afterUTC)
            throws InterruptedException, BucketAccessException {
        if (hasSynchronizationMonitor()) {
            throw new BucketAccessException("Unable to determine whether \"" + pathInBucket + "\" in \""
                    + getFullyQualifiedBucketName()
                    + "\" is synchronized. Please register a synchronization monitor when creating the bucket object.");
        } else {
            return this.monitor.isObjectSynchronized(this, pathInBucket, afterUTC);
        }
    }

    @Override
    public boolean hasSynchronizationMonitor() {
        return this.monitor == null;
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

    private void uploadContentNonBlocking(final BodyPublisher bodyPublisher, final String pathInBucket,
            final String contentDescription) throws InterruptedException, BucketAccessException {
        final URI uri = createWriteUri(pathInBucket);
        LOGGER.info(() -> "Uploading \"" + contentDescription + "\" to bucket \"" + this.bucketFsName + "/"
                + this.bucketName + "\": \"" + uri + "\"");
        try {
            final int statusCode = httpPut(uri, bodyPublisher);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                LOGGER.severe(
                        () -> statusCode + ": Failed to upload \"" + contentDescription + "\" to \"" + uri + "\"");
                throw new BucketAccessException("Unable to upload file \"" + contentDescription + "\" to ", statusCode,
                        uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("I/O error trying to upload \"" + contentDescription + "\" to ", uri,
                    exception);
        }
        LOGGER.fine(() -> "Successfully uploaded to \"" + uri + "\"");
        recordUploadInHistory(pathInBucket);
    }

    private void recordUploadInHistory(final String pathInBucket) {
        final Instant now = Instant.now();
        LOGGER.fine(() -> "Recorded upload to \"" + pathInBucket + "\" at " + now + " in upload history");
        this.uploadHistory.put(pathInBucket, now);
    }

    private URI createWriteUri(final String pathInBucket) throws BucketAccessException {
        try {
            return new URI("http", null, this.ipAddress, this.port, "/" + this.bucketName + "/" + pathInBucket, null,
                    null).normalize();
        } catch (final URISyntaxException exception) {
            throw new BucketAccessException("Unable to create write URI.", exception);
        }
    }

    private int httpPut(final URI uri, final BodyPublisher bodyPublisher) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri) //
                .PUT(bodyPublisher) //
                .header("Authorization", encodeBasicAuth(true)) //
                .build();
        final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
        return response.statusCode();
    }

    private String encodeBasicAuth(final boolean write) {
        return "Basic " + Base64.getEncoder() //
                .encodeToString((write ? ("w:" + this.writePassword) : ("r:" + this.readPassword)).getBytes());
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

    /**
     * Determine if the object at the given path has one of the archive formats that BucketFS can automatically expand.
     *
     * @param pathInBucket path to the object in the bucket
     * @return {@code true} if the object has an archive format that BucketFS can automatically expand.
     */
    public static boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final String extension : SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
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
     * Create builder for a {@link WriteEnabledBucket}.
     *
     * @return builder
     */
    @SuppressWarnings("squid:S1452")
    public static Builder<? extends Builder<?>> builder() {
        return new Builder<>();
    }

    /**
     * Builder for {@link WriteEnabledBucket} objects.
     *
     * @param <T> type for self pointer to inheritable builder
     */
    public static class Builder<T extends Builder<T>> extends ReadEnabledBucket.Builder<Builder<T>> {
        private String writePassword;
        private BucketFsMonitor monitor;

        @SuppressWarnings("unchecked")
        @Override
        protected T self() {
            return (T) this;
        }

        /**
         * Set the write password.
         *
         * @param writePassword write password to set
         * @return Builder instance for fluent programming
         */
        public T writePassword(final String writePassword) {
            this.writePassword = writePassword;
            return self();
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
         * Build a new {@link WriteEnabledBucket} instance.
         *
         * @return bucket instance
         */
        @Override
        public WriteEnabledBucket build() {
            return new WriteEnabledBucket(this);
        }
    }
}