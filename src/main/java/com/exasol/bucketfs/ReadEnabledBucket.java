package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketOperation.DOWNLOAD;
import static com.exasol.bucketfs.list.ListingRetriever.removeLeadingSeparator;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.jsonrpc.CommandFactory;
import com.exasol.bucketfs.list.BucketContentLister;
import com.exasol.bucketfs.list.ListingRetriever;

/**
 * Bucket that support read access like listing contents and downloading files.
 */
public class ReadEnabledBucket implements ReadOnlyBucket {
    private static final Logger LOGGER = Logger.getLogger(ReadEnabledBucket.class.getName());
    private static final String BUCKET_ROOT = "";

    /**
     * BucketFs name
     */
    protected final String serviceName;
    /**
     * Bucket name
     */
    protected final String bucketName;
    /** Protocol for accessing the bucket ({@code http} or {@code https}). */
    protected final String protocol;
    /**
     * Host or IP address
     */
    protected final String host;
    /**
     * Port
     */
    protected final int port;
    /**
     * Read password
     */
    protected final String readPassword;
    /**
     * Upload history
     */
    protected final Map<String, Instant> uploadHistory = new HashMap<>();
    private final HttpClient client;

    /**
     * Create a new instance of a bucket that support reading.
     *
     * @param builder builder from which the bucket should be constructed
     */
    protected ReadEnabledBucket(final Builder<? extends Builder<?>> builder) {
        this.serviceName = builder.serviceName != null ? builder.serviceName : BucketConstants.DEFAULT_BUCKETFS;
        this.bucketName = Objects.requireNonNull(builder.bucketName, "bucketName");
        this.protocol = Objects.requireNonNull(builder.protocol, "protocol");
        this.host = Objects.requireNonNull(builder.host, "host");
        this.port = builder.port;
        this.readPassword = builder.readPassword;
        this.client = builder.httpClientBuilder.build();
    }

    @Override
    public String getBucketFsName() {
        return this.serviceName;
    }

    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    @Override
    public String getFullyQualifiedBucketName() {
        return this.serviceName + BucketConstants.PATH_SEPARATOR + this.bucketName;
    }

    @Override
    public String getReadPassword() {
        return this.readPassword;
    }

    @Override
    // [impl->dsn~bucket-lists-its-contents~2]
    public List<String> listContents() throws BucketAccessException {
        return listContents(BUCKET_ROOT, false);
    }

    @Override
    // [impl->dsn~bucket-lists-its-contents-recursively~1]
    public List<String> listContentsRecursively() throws BucketAccessException {
        return listContents(BUCKET_ROOT, true);
    }

    @Override
    // [impl->dsn~bucket-lists-files-with-common-prefix~1]
    // [impl->dsn~bucket-lists-file-and-directory-with-identical-name~1]
    // [impl->dsn~bucket-lists-directories-with-suffix~1]
    public List<String> listContents(final String path) throws BucketAccessException {
        return listContents(path, false);
    }

    @Override
    // [impl->dsn~bucket-lists-its-contents-recursively~1]
    public List<String> listContentsRecursively(final String path) throws BucketAccessException {
        return listContents(path, true);
    }

    private List<String> listContents(final String path, final boolean recursive) throws BucketAccessException {
        final URI uri = createPublicReadURI("");
        final ListingRetriever contentLister = new ListingRetriever(this.client);
        return new BucketContentLister(uri, contentLister, this.readPassword) //
                .retrieve(removeLeadingSeparator(path), recursive);
    }

    // [impl->dsn~tls-configuration~1]
    private URI createPublicReadURI(final String pathInBucket) {
        final String suffix = this.bucketName + "/" + removeLeadingSeparator(pathInBucket);
        return ListingRetriever.publicReadUri(this.protocol, this.host, this.port, suffix);
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
            HttpResponseEvaluator.evaluate(uri, DOWNLOAD, response.statusCode());
        } catch (final IOException exception) {
            throw BucketAccessException.downloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw BucketAccessException.downloadInterruptedException(uri, DOWNLOAD);
        }
    }

    private HttpRequest createGetRequest(final URI uri) {
        return HttpRequest.newBuilder(uri) //
                .GET() //
                .header("Authorization", encodeBasicAuthForReading()) //
                .build();
    }

    // [impl->dsn~downloading-a-file-from-a-bucket-as-string~1]
    @Override
    public String downloadFileAsString(final String pathInBucket) throws BucketAccessException {
        final var uri = createPublicReadURI(pathInBucket);
        LOGGER.fine(() -> "Downloading  file from bucket '" + this + "' at '" + uri + "'");
        final var response = requestFileOnBucketAsString(uri);
        HttpResponseEvaluator.evaluate(uri, DOWNLOAD, response.statusCode());
        return response.body();
    }

    private HttpResponse<String> requestFileOnBucketAsString(final URI uri) throws BucketAccessException {
        try {
            final var request = createGetRequest(uri);
            return this.client.send(request, BodyHandlers.ofString());
        } catch (final IOException exception) {
            throw BucketAccessException.downloadIoException(uri, DOWNLOAD, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw BucketAccessException.downloadInterruptedException(uri, DOWNLOAD);
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
        return (this.serviceName == null ? (this.port + ":") : (this.serviceName + "/")) + this.bucketName;
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
        private String serviceName;
        private String bucketName;
        private String host;
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
         * Set the service name.
         *
         * @param serviceName name of the BucketFS service
         * @return Builder instance for fluent programming
         */
        public T serviceName(final String serviceName) {
            this.serviceName = serviceName;
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
        // [impl->dsn~tls-configuration~1]
        public T useTls(final boolean useTls) {
            this.protocol = useTls ? "https" : "http";
            return self();
        }

        /**
         * Set the host of the BucketFS service.
         *
         * @param host host of the BucketFS service
         * @return Builder instance for fluent programming
         */
        public T host(final String host) {
            this.host = host;
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
        // [impl->dsn~custom-tls-certificate~1]
        public T certificate(final X509Certificate certificate) {
            this.httpClientBuilder.certificate(certificate);
            return self();
        }

        /**
         * Update the certificate specified via {@link #certificate(X509Certificate)} to allow an additional host name,
         * e.g. {@code localhost}.
         * <p>
         * This is useful when a self-signed certificate does not contain the required subject alternative name (SAN).
         * 
         * @param hostName additional hostname to allow
         * @return this instance for method chaining
         */
        public T allowAlternativeHostName(final String hostName) {
            this.httpClientBuilder.allowAlternativeHostName(hostName);
            return self();
        }

        /**
         * Update the certificate specified via {@link #certificate(X509Certificate)} to allow an additional IP address,
         * e.g. {@code 127.0.0.1}.
         * <p>
         * This is useful when a self-signed certificate does not contain the required subject alternative name (SAN).
         * 
         * @param ipAddress additional IP address to allow
         * @return this instance for method chaining
         */
        public T allowAlternativeIpAddress(final String ipAddress) {
            this.httpClientBuilder.allowAlternativeIPAddress(ipAddress);
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
