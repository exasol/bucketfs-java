package com.exasol.bucketfs;

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

    /**
     * @return name of the BucketFS filesystem this bucket belongs to
     */
    @Override
    public String getBucketFsName() {
        return this.bucketFsName;
    }

    /**
     * @return name of the bucket
     */
    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * Get the fully qualified name of the bucket.
     *
     * @return fully qualified name consisting of service name and bucket name
     */
    @Override
    public String getFullyQualifiedBucketName() {
        return this.bucketFsName + BucketConstants.PATH_SEPARATOR + this.bucketName;
    }

    /**
     * Get the read password for the bucket.
     *
     * @return read password
     */
    @Override
    public String getReadPassword() {
        return this.readPassword;
    }

    /**
     * List the contents of a bucket.
     *
     * @return bucket contents
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    @Override
    public List<String> listContents() throws BucketAccessException, InterruptedException {
        return listContents(BUCKET_ROOT);
    }

    /**
     * List the contents of a path inside a bucket.
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
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
        return pathInBucket.endsWith(BucketConstants.PATH_SEPARATOR) ? pathInBucket + localPath.getFileName() : pathInBucket;
    }

    /**
     * Download a file from a bucket to a local filesystem.
     *
     * @param pathInBucket path of the file in BucketFS
     * @param localPath    local path the file is downloaded to
     * @throws InterruptedException  if the file download was interrupted
     * @throws BucketAccessException if the local file does not exist or is not accessible or if the download failed
     */
    @Override
    public void downloadFile(final String pathInBucket, final Path localPath)
            throws InterruptedException, BucketAccessException {
        final URI uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket \"" + this.bucketFsName + "/" + this.bucketName + "\": \""
                + uri + "\" to \"" + localPath + "\"");
        try {
            final int statusCode = httpGet(uri, localPath);
            if (statusCode != HttpURLConnection.HTTP_OK) {
                LOGGER.severe(() -> statusCode + ": Failed to download \"" + uri + "\" to file \"" + localPath + "\"");
                throw new BucketAccessException("Unable to downolad file \"" + localPath + "\" from ", statusCode, uri);
            }
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to upload file \"" + localPath + "\" from ", uri, exception);
        }
        LOGGER.fine(() -> "Successfully downloaded file to \"" + localPath + "\"");
    }

    private int httpGet(final URI uri, final Path localPath) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri) //
                .GET() //
                .header("Authorization", encodeBasicAuthForReading()) //
                .build();
        final HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
        Files.write(localPath, response.body().getBytes());
        return response.statusCode();
    }

    private String encodeBasicAuthForReading() {
        return "Basic " + Base64.getEncoder().encodeToString(("r:" + this.readPassword).getBytes());
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
