package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketOperation.DELETE;
import static com.exasol.bucketfs.BucketOperation.UPLOAD;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.exasol.bucketfs.uploadnecessity.UploadAlwaysStrategy;
import com.exasol.bucketfs.uploadnecessity.UploadNecessityCheckStrategy;

/**
 * An abstraction for a bucket inside Exasol's BucketFS.
 */
public class WriteEnabledBucket extends ReadEnabledBucket implements UnsynchronizedBucket {
    private static final Logger LOGGER = Logger.getLogger(WriteEnabledBucket.class.getName());
    private final String writePassword;
    private UploadNecessityCheckStrategy uploadNecessityCheckStrategy = new UploadAlwaysStrategy();

    /**
     * @param builder builder
     */
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
    public UploadResult uploadFileNonBlocking(final Path localPath, final String pathInBucket)
            throws BucketAccessException, FileNotFoundException {
        final var extendedPathInBucket = extendPathInBucketDownToFilename(localPath, pathInBucket);
        if (this.uploadNecessityCheckStrategy.isUploadNecessary(localPath, pathInBucket, this)) {
            final var uri = createWriteUri(extendedPathInBucket);
            uploadWithBodyPublisher(uri, BodyPublishers.ofFile(localPath), "file '" + localPath + "'");
            recordUploadInHistory(pathInBucket);
            return new UploadResult(true);
        } else {
            LOGGER.fine("Skipping upload since the " + this.uploadNecessityCheckStrategy.getClass().getSimpleName()
                    + " decided it's not necessary.");
            return new UploadResult(false);
        }
    }

    /**
     * Upload with body publisher.
     *
     * @param uri       uri
     * @param publisher publisher
     * @param what      what
     * @throws BucketAccessException BucketAccessException
     */
    protected void uploadWithBodyPublisher(final URI uri, final BodyPublisher publisher, final String what)
            throws BucketAccessException {
        LOGGER.fine(() -> "Uploading " + what + " to bucket '" + this + "' at '" + uri + "'");
        requestUpload(uri, publisher);
        LOGGER.fine(() -> "Successfully uploaded " + what + " to '" + uri + "'");
    }

    // [impl->dsn~tls-configuration~1]
    private URI createWriteUri(final String pathInBucket) throws BucketAccessException {
        try {
            return new URI(this.protocol, null, this.host, this.port, "/" + this.bucketName + "/" + pathInBucket, null,
                    null).normalize();
        } catch (final URISyntaxException exception) {
            throw new BucketAccessException("Unable to create write URI for path '" + pathInBucket + "'.", exception);
        }
    }

    private void requestUpload(final URI uri, final BodyPublisher bodyPublisher) throws BucketAccessException {
        try {
            final var request = HttpRequest.newBuilder(uri) //
                    .PUT(bodyPublisher) //
                    .header("Authorization", encodeBasicAuth(true)) //
                    .build();
            final var response = getClient().send(request, BodyHandlers.ofString());
            final var statusCode = response.statusCode();
            HttpResponseEvaluator.evaluate(uri, UPLOAD, statusCode);
        } catch (final IOException exception) {
            throw createUploadIoException(uri, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createUploadInterruptedException(uri);
        }
    }

    /**
     * Create UploadIoException.
     *
     * @param uri       uri
     * @param exception exception
     * @return BucketAccessException
     */
    protected BucketAccessException createUploadIoException(final URI uri, final IOException exception) {
        return new BucketAccessException(
                messageBuilder("E-BFSJ-7").message("I/O error trying to upload to {{URI}}", uri).toString(), exception);
    }

    /**
     * Create UploadInterruptedException.
     *
     * @param uri uri
     * @return BucketAccessException
     */
    protected BucketAccessException createUploadInterruptedException(final URI uri) {
        return new BucketAccessException(
                messageBuilder("E-BFSJ-6").message("Interrupted trying to upload {{URI}}.", uri).toString());
    }

    /**
     * Record the upload in the internal upload history of this bucket object.
     * <p>
     * This is mainly necessary to work around issues with detecting repeated uploads. Under certain circumstances they
     * need to be delayed and the upload history helps with that.
     * </p>
     *
     * @param pathInBucket path in the bucket to which an upload happened.
     */
    protected void recordUploadInHistory(final String pathInBucket) {
        final var now = Instant.now();
        LOGGER.finest(() -> "Recorded upload to '" + pathInBucket + "' at " + now + " in upload history");
        this.uploadHistory.put(pathInBucket, now);
    }

    private String encodeBasicAuth(final boolean write) {
        return "Basic " + Base64.getEncoder() //
                .encodeToString((write ? ("w:" + this.writePassword) : ("r:" + this.readPassword)).getBytes());
    }

    @Override
    public void uploadStringContentNonBlocking(final String content, final String pathInBucket)
            throws BucketAccessException, TimeoutException {
        final var uri = createWriteUri(pathInBucket);
        final var excerpt = (content.length() > 20) ? content.substring(0, 20) + "..." : content;
        uploadWithBodyPublisher(uri, BodyPublishers.ofString(content), "string content '" + excerpt + "'");
        recordUploadInHistory(pathInBucket);
    }

    // [impl->dsn~uploading-input-stream-to-bucket~1]
    @Override
    public void uploadInputStreamNonBlocking(final Supplier<InputStream> inputStreamSupplier, final String pathInBucket)
            throws BucketAccessException, TimeoutException {
        final var uri = createWriteUri(pathInBucket);
        uploadWithBodyPublisher(uri, BodyPublishers.ofInputStream(inputStreamSupplier), "content of input stream");
        recordUploadInHistory(pathInBucket);
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

    @Override
    // [impl->dsn~delete-a-file-from-a-bucket~1]
    public void deleteFileNonBlocking(final String filenameInBucket) throws BucketAccessException {
        try {
            final var uri = createWriteUri(filenameInBucket);
            final var request = HttpRequest.newBuilder(uri) //
                    .DELETE() //
                    .header("Authorization", encodeBasicAuth(true)) //
                    .build();
            final var response = getClient().send(request, BodyHandlers.ofString());
            final var statusCode = response.statusCode();
            HttpResponseEvaluator.evaluate(uri, DELETE, statusCode);
        } catch (final IOException exception) {
            throw getDeleteFailedException(filenameInBucket, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw getDeleteFailedException(filenameInBucket, exception);
        }
    }

    private BucketAccessException getDeleteFailedException(final String filenameInBucket, final Exception exception) {
        return new BucketAccessException(messageBuilder("E-BFSJ-12")
                .message("Failed to delete {{file}} from BucketFS.", filenameInBucket).toString(), exception);
    }

    @Override
    public void setUploadNecessityCheckStrategy(final UploadNecessityCheckStrategy uploadNecessityCheckStrategy) {
        this.uploadNecessityCheckStrategy = uploadNecessityCheckStrategy;
    }

    /**
     * Builder for {@link WriteEnabledBucket} objects.
     *
     * @param <T> type for self-pointer to inheritable builder
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
