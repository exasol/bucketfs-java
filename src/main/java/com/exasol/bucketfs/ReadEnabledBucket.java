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
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.jsonrpc.CommandFactory;

/**
 * Bucket that support read access like listing contents and downloading files.
 */
public class ReadEnabledBucket implements ReadOnlyBucket {
    private static final Logger LOGGER = Logger.getLogger(ReadEnabledBucket.class.getName());
    private static final String BUCKET_ROOT = "";

    /**
     * bucketFs name
     */
    protected final String bucketFsName;
    /**
     * bucket name
     */
    protected final String bucketName;
    private final String protocol;
    /**
     * ip address
     */
    protected final String ipAddress;
    /**
     * port
     */
    protected final int port;
    /**
     * read password
     */
    protected final String readPassword;
    /**
     * upload history
     */
    protected final Map<String, Instant> uploadHistory = new HashMap<>();
    private final HttpClient client;

    /**
     * @param builder builder
     */
    protected ReadEnabledBucket(final Builder<? extends Builder<?>> builder) {
        this.bucketFsName = builder.bucketFsName;
        this.bucketName = builder.bucketName;
        this.protocol = builder.protocol;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.readPassword = builder.readPassword;
        this.client = builder.httpClientBuilder.build();
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
        LOGGER.fine(() -> "Listing contents of bucket under URI '" + uri + "'");
        return requestListing(path, uri);
    }

    private List<String> requestListing(final String path, final URI uri) throws BucketAccessException {
        try {
            final var request = HttpRequest.newBuilder(uri).build();
            final var response = this.client.send(request, BodyHandlers.ofString());
            evaluateRequestStatus(uri, LIST, response.statusCode());
            final var list = parseContentListResponseBody(response, removeLeadingSlash(path));
            if (list.isEmpty()) {
                throw createPathToBeListedNotFoundException(path);
            } else {
                return list;
            }
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, LIST, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, LIST);
        }
    }

    private BucketAccessException createPathToBeListedNotFoundException(final String path) {
        return new BucketAccessException(messageBuilder("E-BFSJ-11")
                .message("Unable to list contents of {{path}} in bucket {{bucket}}: No such file or directory.", path,
                        this)
                .toString());
    }

    private String removeLeadingSlash(final String path) {
        if (path.startsWith(BucketConstants.PATH_SEPARATOR)) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private URI createPublicReadURI(final String pathInBucket) {
        return URI.create(this.protocol + "://" + this.ipAddress + ":" + this.port + "/" + this.bucketName + "/"
                + removeLeadingSlash(pathInBucket));
    }

    // [impl->dsn~bucket-lists-files-with-common-prefix~1]
    // [impl->dsn~bucket-lists-file-and-directory-with-identical-name~1]
    // [impl->dsn~bucket-lists-directories-with-suffix~1]
    private List<String> parseContentListResponseBody(final HttpResponse<String> response, final String path) {
        return Arrays.stream(response.body().split("\\s+")) //
                .map(this::removeLeadingSlash) //
                .filter(e -> e.startsWith(path)) // keep only entries with path as prefix
                .map(e -> e.substring(path.length())) // cut of path prefix
                .map(this::extractFirstPathComponent) //
                .distinct() // ensure only unique entries
                .sorted() //
                .collect(Collectors.toList());
    }

    private String extractFirstPathComponent(final String path) {
        if (path.contains(BucketConstants.PATH_SEPARATOR)) {
            return path.substring(0, path.indexOf(BucketConstants.PATH_SEPARATOR) + 1);
        } else {
            return path;
        }
    }

    /**
     * Extends path in bucket down to filename.
     *
     * @param localPath    localPath
     * @param pathInBucket pathInBucket
     * @return String
     */
    protected String extendPathInBucketDownToFilename(final Path localPath, final String pathInBucket) {
        return pathInBucket.endsWith(BucketConstants.PATH_SEPARATOR) ? pathInBucket + localPath.getFileName()
                : pathInBucket;
    }

    // [impl->dsn~downloading-a-file-from-a-bucket~1]
    @Override
    public void downloadFile(final String pathInBucket, final Path localPath) throws BucketAccessException {
        final var uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket '" + this + "' at '" + uri + "' to '" + localPath + "'");
        requestFileOnBucket(uri, localPath);
        LOGGER.fine(() -> "Successfully downloaded file to '" + localPath + "'");
    }

    private void requestFileOnBucket(final URI uri, final Path localPath) throws BucketAccessException {
        try {
            final var request = createGetRequest(uri);
            final var response = this.client.send(request, BodyHandlers.ofFile(localPath));
            evaluateRequestStatus(uri, DOWNLOAD, response.statusCode());
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, DOWNLOAD);
        }
    }

    private HttpRequest createGetRequest(final URI uri) {
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
        LOGGER.fine(() -> "Downloading  file from bucket '" + this + "' at '" + uri + "'");
        final var response = requestFileOnBucketAsString(uri);
        evaluateRequestStatus(uri, DOWNLOAD, response.statusCode());
        return response.body();
    }

    private HttpResponse<String> requestFileOnBucketAsString(final URI uri) throws BucketAccessException {
        try {
            final var request = createGetRequest(uri);
            return this.client.send(request, BodyHandlers.ofString());
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, DOWNLOAD);
        }
    }

    /**
     * Evaluates the request status.
     *
     * @param uri        uri
     * @param operation  operation
     * @param statusCode statusCode
     * @throws BucketAccessException BucketAccessException
     */
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
                    .message("Unable to perform {{operation|uq}} {{URI}}. HTTP status {{status}}.", operation, uri,
                            statusCode)
                    .toString());
        }
    }

    private String encodeBasicAuthForReading() {
        return "Basic " + Base64.getEncoder().encodeToString(("r:" + this.readPassword).getBytes());
    }

    /**
     * Get the http client.
     *
     * @return http client
     */
    protected HttpClient getClient() {
        return this.client;
    }

    @Override
    public String toString() {
        return (this.bucketFsName == null ? (this.port + ":") : (this.bucketFsName + "/")) + this.bucketName;
    }

    /**
     * Returns a builder.
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
        private String protocol = "http";
        private String bucketFsName;
        private String bucketName;
        private String ipAddress;
        private int port;
        private String readPassword;
        private final HttpClientBuilder httpClientBuilder;

        Builder(final HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
        }

        /**
         * Create a new instance of {@link Builder}.
         */
        protected Builder() {
            this(new HttpClientBuilder());
        }

        /**
         * Get self.
         *
         * @return self
         */
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
         * Define if you want to use TLS/HTTPS for connecting to the server. Defaults to plain text HTTP.
         *
         * @param useTls {@code true} to use the TLS/HTTPS protocol, {@code false} to use plain text HTTP (default)
         * @return Builder instance for fluent programming
         */
        public T useTls(final boolean useTls) {
            this.protocol = useTls ? "https" : "http";
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
         * Set the port the BucketFS service listens on. Make sure to also call {@link #useTls(boolean)} with argument
         * {@code false} if this is an HTTP port or {@code true} if this is an HTTPS port.
         *
         * @param port HTTP or HTTPS port the BucketFS service listens on
         * @return Builder instance for fluent programming
         */
        public T port(final int port) {
            this.port = port;
            return self();
        }

        /**
         * Set the port the BucketFS service listens on. Make sure to also call {@link #useTls(boolean)} with argument
         * {@code false} if this is an HTTP port or {@code true} if this is an HTTPS port.
         *
         * @param port HTTP or HTTPS port the BucketFS service listens on
         * @return Builder instance for fluent programming
         * @deprecated use {@link #port(int)} instead.
         */
        @Deprecated
        public T httpPort(final int port) {
            return this.port(port);
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
         * Define if TLS errors should raise an error when executing requests or if they should be ignored. Setting this
         * to <code>false</code> is required as the docker-db uses a self-signed certificate.
         * <p>
         * Defaults to raise TLS errors.
         * <p>
         * Mutually exclusive with setting {@link #raiseTlsErrors} to {@code false}.
         *
         * @param raise <code>true</code> if the {@link CommandFactory} should fail for TLS errors, <code>false</code>
         *              if it should ignore TLS errors.
         * @return Builder instance for fluent programming
         */
        public T raiseTlsErrors(final boolean raise) {
            this.httpClientBuilder.raiseTlsErrors(raise);
            return self();
        }

        /**
         * Use the given certificate for TLS connections.
         * <p>
         * Defaults to using the certificates from the JVMs default key store.
         * <p>
         * Mutually exclusive with setting {@link #raiseTlsErrors} to {@code false}.
         *
         * @param certificate certificate to use
         * @return Builder instance for fluent programming
         */
        public T certificate(final X509Certificate certificate) {
            this.httpClientBuilder.certificate(certificate);
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