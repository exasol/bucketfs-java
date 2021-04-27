package com.exasol.bucketfs;

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
import java.util.Base64;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class WriteEnabledBucket extends ReadEnabledBucket implements UnsynchronizedBucket {
    private final String writePassword;

    protected WriteEnabledBucket(final Builder<? extends Builder<?>> builder) {
        super(builder);
        this.writePassword = builder.writePassword;
    }

    @Override
    public String getWritePassword() {
        return this.writePassword;
    }

    // [impl->dsn~uploading-to-bucket~1]
    @Override
    public void uploadFileNonBlocking(final Path localPath, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final String extendedPathInBucket = extendPathInBucketDownToFilename(localPath, pathInBucket);
        try {
            uploadContentNonBlocking(BodyPublishers.ofFile(localPath), extendedPathInBucket,
                    "file " + extendedPathInBucket);
        } catch (final IOException exception) {
            throw new BucketAccessException("I/O failed to open file \"" + localPath + "\" for upload to BucketFS.",
                    exception);
        }
    }

    protected void uploadContentNonBlocking(final BodyPublisher bodyPublisher, final String pathInBucket,
            final String contentDescription) throws InterruptedException, BucketAccessException {
        final URI uri = createWriteUri(pathInBucket);
        LOGGER.info(() -> "Uploading \"" + contentDescription + "\" to bucket \"" + this + "\" at \"" + uri + "\"");
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

    @Override
    public void uploadStringContentNonBlocking(final String content, final String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final String excerpt = (content.length() > 20) ? content.substring(0, 20) + "..." : content;
        final String description = "text " + excerpt;
        uploadContentNonBlocking(BodyPublishers.ofString(content), pathInBucket, description);
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