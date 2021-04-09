package com.exasol.bucketfs;

import com.exasol.errorreporting.ExaError;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

/**
 * Bucket that support read access like listing contents and downloading files.
 */
public class ReadEnabledBucket implements ReadOnlyBucket {
    protected static final Logger LOGGER = Logger.getLogger(ReadEnabledBucket.class.getName());
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
    public List<String> listContents() throws BucketAccessException, InterruptedException {
        return listContents(BUCKET_ROOT);
    }

    @Override
    public List<String> listContents(final String path) throws BucketAccessException, InterruptedException {
        final URI uri = createPublicReadURI(BUCKET_ROOT);
        LOGGER.fine(() -> "Listing contents of bucket under URI \"" + uri + "\"");
        try {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return parseContentListResponseBody(response, removeLeadingSlash(path));
            } else {
                throw new BucketAccessException("Unable to list contents of bucket.", response.statusCode(), uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to list contents of bucket.", uri, exception);
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
        final String[] items = response.body().split("\\s+");
        final List<String> contents = new ArrayList<>(items.length);
        for (final String item : items) {
            final String relativeItem = removeLeadingSlash(item);
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
    public void downloadFile(final String pathInBucket, final Path localPath)
            throws InterruptedException, BucketAccessException {
        final URI uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket \"" + this.bucketFsName + "/" + this.bucketName + "\": \""
                + uri + "\" to \"" + localPath + "\"");
        try {
            final String content = httpGet(uri);
            Files.write(localPath, content.getBytes());
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to download file \"" + localPath + "\" from ", uri, exception);
        }
        LOGGER.fine(() -> "Successfully downloaded file to \"" + localPath + "\"");
    }

    // [impl->dsn~downloading-a-file-from-a-bucket-as-string~1]
    @Override
    public String downloadFileAsString(final String pathInBucket)
            throws InterruptedException, BucketAccessException {
        final URI uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket \"" + this.bucketFsName + "/" + this.bucketName + "\": \""
                + uri + "\"");
        try {
            return httpGet(uri);
        } catch (final IOException exception) {
            throw new BucketAccessException(
                    "Unable to download file from BucketFS as string.", uri, exception);
        }
    }

    private String httpGet(final URI uri) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri) //
                .GET() //
                .header("Authorization", encodeBasicAuthForReading()) //
                .build();
        final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
        checkHttpStatusCode(response.statusCode());
        return response.body();
    }

    private void checkHttpStatusCode(int statusCode) throws IOException {
        if (statusCode != HttpURLConnection.HTTP_OK) {
            throw new IOException(ExaError.messageBuilder("E-BFSJ-1").message("Http status code {{status code}} != 200 (HTTP-OK)", statusCode).toString());
        }
    }

    private String encodeBasicAuthForReading() {
        return "Basic " + Base64.getEncoder().encodeToString(("r:" + this.readPassword).getBytes());
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