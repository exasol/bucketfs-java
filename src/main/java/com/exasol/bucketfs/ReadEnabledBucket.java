package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketOperation.DOWNLOAD;
import static com.exasol.bucketfs.BucketOperation.LIST;
import static com.exasol.errorreporting.ExaError.messageBuilder;
import static java.net.HttpURLConnection.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

/**
 * Bucket that support read access like listing contents and downloading files.
 */
public class ReadEnabledBucket implements ReadOnlyBucket {
    private static final Logger LOGGER = Logger.getLogger(ReadEnabledBucket.class.getName());
    private static final String BUCKET_ROOT = "";
    protected final String bucketFsName;
    protected final String bucketName;
    protected final String ipAddress;
    protected final int port;
    protected final String readPassword;
    protected final HttpClient client = HttpClient.newBuilder().build();
    protected final Map<String, Instant> uploadHistory = new HashMap<>();

    protected ReadEnabledBucket(final Builder<? extends Builder<?>> builder) {
        this.bucketFsName = builder.bucketFsName;
        this.bucketName = builder.bucketName;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.readPassword = builder.readPassword;
    }

    @Override
    public String getBucketFsName() {
        return this.bucketFsName;
    }

    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    @Override
    public String getFullyQualifiedBucketName() {
        return this.bucketFsName + BucketConstants.PATH_SEPARATOR + this.bucketName;
    }

    @Override
    public String getReadPassword() {
        return this.readPassword;
    }

    @Override
    // [impl->dsn~bucket-lists-its-contents~1]
    public List<String> listContents() throws BucketAccessException {
        return listContents(BUCKET_ROOT);
    }

    @Override
    public List<String> listContents(final String path) throws BucketAccessException {
        final var uri = createPublicReadURI(BUCKET_ROOT);
        LOGGER.fine(() -> "Listing contents of bucket under URI \"" + uri + "\"");
        return requestListing(path, uri);
    }

    private List<String> requestListing(final String path, final URI uri) throws BucketAccessException {
        try {
            final var request = HttpRequest.newBuilder(uri).build();
            final var response = this.client.send(request, BodyHandlers.ofString());
            evaluateRequestStatus(uri, LIST, response.statusCode());
            return parseContentListResponseBody(response, removeLeadingSlash(path));
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, LIST, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, LIST);
        }
    }

    private String removeLeadingSlash(final String path) {
        if (path.startsWith(BucketConstants.PATH_SEPARATOR)) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private URI createPublicReadURI(final String pathInBucket) {
        return URI.create("http://" + this.ipAddress + ":" + this.port + "/" + this.bucketName + "/"
                + removeLeadingSlash(pathInBucket));
    }

    private List<String> parseContentListResponseBody(final HttpResponse<String> response, final String path) {
        final var items = response.body().split("\\s+");
        final var contents = new ArrayList<String>(items.length);
        for (final var item : items) {
            final var relativeItem = removeLeadingSlash(item);
            if (relativeItem.startsWith(path)) {
                contents.add(extractFirstPathComponent(relativeItem.substring(path.length(), relativeItem.length())));
            }
        }
        return contents;
    }

    private String extractFirstPathComponent(final String path) {
        if (path.contains(BucketConstants.PATH_SEPARATOR)) {
            return path.substring(0, path.indexOf(BucketConstants.PATH_SEPARATOR));
        } else {
            return path;
        }
    }

    protected String extendPathInBucketDownToFilename(final Path localPath, final String pathInBucket) {
        return pathInBucket.endsWith(BucketConstants.PATH_SEPARATOR) ? pathInBucket + localPath.getFileName()
                : pathInBucket;
    }

    // [impl->dsn~downloading-a-file-from-a-bucket~1]
    @Override
    public void downloadFile(final String pathInBucket, final Path localPath) throws BucketAccessException {
        final var uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket \"" + this + "\" at \"" + uri + "\" to \"" + localPath + "\"");
        requestFileOnBucket(uri, localPath);
        LOGGER.fine(() -> "Successfully downloaded file to \"" + localPath + "\"");
    }

    private void requestFileOnBucket(final URI uri, final Path localPath) throws BucketAccessException {
        try {
            final var request = createRequest(uri);
            final var response = this.client.send(request, BodyHandlers.ofFile(localPath));
            evaluateRequestStatus(uri, DOWNLOAD, response.statusCode());
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, DOWNLOAD);
        }
    }

    private HttpRequest createRequest(final URI uri) {
        return HttpRequest.newBuilder(uri) //
                .GET() //
                .header("Authorization", encodeBasicAuthForReading()) //
                .build();
    }

    private BucketAccessException createDownloadIoException(final URI uri, final BucketOperation operation,
            final IOException exception) {
        return new BucketAccessException(messageBuilder("E-BFSJ-5")
                .message("I/O error trying to {{operation|uq}} {{URI}}", operation, uri).toString(), exception);
    }

    private BucketAccessException createDownloadInterruptedException(final URI uri, final BucketOperation operation) {
        return new BucketAccessException(messageBuilder("E-BFSJ-4")
                .message("Interrupted trying to {{operation|uq}} {{URI}}.", operation, uri).toString());
    }

    // [impl->dsn~downloading-a-file-from-a-bucket-as-string~1]
    @Override
    public String downloadFileAsString(final String pathInBucket) throws BucketAccessException {
        final var uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket \"" + this + "\" at \"" + uri + "\"");
        final var response = requestFileOnBucketAsString(uri);
        evaluateRequestStatus(uri, DOWNLOAD, response.statusCode());
        return response.body();
    }

    private HttpResponse<String> requestFileOnBucketAsString(final URI uri) throws BucketAccessException {
        try {
            final var request = createRequest(uri);
            return this.client.send(request, BodyHandlers.ofString());
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, DOWNLOAD);
        }
    }

    protected void evaluateRequestStatus(final URI uri, final BucketOperation operation, final int statusCode)
            throws BucketAccessException {
        switch (statusCode) {
        case HTTP_OK:
            return;
        case HTTP_NOT_FOUND:
            throw new BucketAccessException(messageBuilder("E-BFSJ-2")
                    .message("File or directory not found trying to {{operation|uq}} {{URI}}.", operation, uri)
                    .toString());
        case HTTP_FORBIDDEN:
            throw new BucketAccessException(messageBuilder("E-BFSJ-3")
                    .message("Access denied trying to {{operation|uq}} {{URI}}.", operation, uri).toString());
        default:
            throw new BucketAccessException(messageBuilder("E-BFSJ-1")
                    .message("Unable do {{operation|uq}} {{URI}}. HTTP status {{status}}.", operation, uri, statusCode)
                    .toString());
        }
    }

    private String encodeBasicAuthForReading() {
        return "Basic " + Base64.getEncoder().encodeToString(("r:" + this.readPassword).getBytes());
    }

    @Override
    public String toString() {
        return (this.bucketFsName == null ? (this.port + ":") : (this.bucketFsName + "/")) + this.bucketName;
    }

    @SuppressWarnings("squid:S1452")
    public static Builder<? extends Builder<?>> builder() {
        return new Builder<>();
    }

    /**
     * Builder for {@link ReadEnabledBucket} objects.
     *
     * @param <T> type for self pointer to inheritable builder
     */
    public static class Builder<T extends Builder<T>> {
        private String bucketFsName;
        private String bucketName;
        private String ipAddress;
        private int port;
        private String readPassword;

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        /**
         * Set the filesystem name.
         *
         * @param bucketFsName name of the BucketFS filesystem
         * @return Builder instance for fluent programming
         */
        public T serviceName(final String bucketFsName) {
            this.bucketFsName = bucketFsName;
            return self();
        }

        /**
         * Set the bucket name.
         *
         * @param bucketName name of the bucket
         * @return Builder instance for fluent programming
         */
        public T name(final String bucketName) {
            this.bucketName = bucketName;
            return self();
        }

        /**
         * Set the IP address of the BucketFS service.
         *
         * @param ipAddress IP Address of the BucketFS service
         * @return Builder instance for fluent programming
         */
        public T ipAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return self();
        }

        /**
         * Set the port the BucketFS service listens on.
         *
         * @param port HTTP port the BucketFS service listens on
         * @return Builder instance for fluent programming
         */
        public T httpPort(final int port) {
            this.port = port;
            return self();
        }

        /**
         * Set the read password.
         *
         * @param readPassword read password to set
         * @return Builder instance for fluent programming
         */
        public T readPassword(final String readPassword) {
            this.readPassword = readPassword;
            return self();
        }

        /**
         * Build a new {@link ReadEnabledBucket} instance.
         *
         * @return read-enabled bucket instance
         */
        public ReadOnlyBucket build() {
            return new ReadEnabledBucket(this);
        }
    }
}