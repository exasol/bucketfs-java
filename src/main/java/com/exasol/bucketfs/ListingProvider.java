package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.bucketfs.BucketOperation.LIST;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ListingProvider {

    private static final Logger LOGGER = Logger.getLogger(ReadEnabledBucket.class.getName());
    private static final String BUCKET_ROOT = "";

    private final HttpClient client;
    private final String protocol;
    private final String host;
    private final int port;
    private final String bucketName;

    private ListingProvider(final Builder builder) {
        this.client = builder.httpClient;
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.bucketName = builder.bucketName;
    }

    public List<String> listContents() throws BucketAccessException {
        return listContents(BUCKET_ROOT);
    }

    public List<String> listContents(final String path) throws BucketAccessException {
        final var uri = createPublicReadURI();
        LOGGER.fine(() -> "Listing contents under URI '" + uri + "'");
        final List<String> list = filter(requestListing(uri), this.bucketName == null ? "" : path);
        if (list.isEmpty()) {
            throw createPathToBeListedNotFoundException(path);
        } else {
            return list;
        }
    }

    private URI createPublicReadURI() {
        final String suffix = (this.bucketName != null ? this.bucketName + "/" : "");
        return URI.create(this.protocol + "://" + this.host + ":" + this.port + "/" + suffix);
    }

    public static String removeLeadingSeparator(final String path) {
        return path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path;
    }

    private String requestListing(final URI uri) throws BucketAccessException {
        try {
            final var request = HttpRequest.newBuilder(uri).build();
            final var response = this.client.send(request, BodyHandlers.ofString());
            HttpRequestStatus.evaluate(uri, LIST, response.statusCode());
            return response.body();
        } catch (final IOException exception) {
            throw createDownloadIoException(uri, LIST, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw createDownloadInterruptedException(uri, LIST);
        }
    }

    private List<String> filter(final String listing, final String path) {
        final String prefix = removeLeadingSeparator(path);
        return Arrays.stream(listing.split("\\s+")) //
                .filter(e -> e.startsWith(prefix)) //
                .sorted() //
                .collect(Collectors.toList());
    }

    private BucketAccessException createPathToBeListedNotFoundException(final String path) {
        return new BucketAccessException(messageBuilder("E-BFSJ-11")
                .message("Unable to list contents of {{path}} in bucket {{bucket}}: No such file or directory.", path,
                        this)
                .toString());
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private HttpClient httpClient;
        private String protocol = "http";
        private String host;
        private int port;
        private String bucketName;

        /**
         * Create a new instance of {@link Builder}.
         */
        protected Builder() {
        }

        /**
         * Set the HTTP client to use for retrieving the listing
         *
         * @param httpClient HTTP client
         * @return Builder instance for fluent programming
         */
        public Builder httpClient(final HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder protocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * Set the bucket name.
         *
         * @param bucketName name of the bucket
         * @return Builder instance for fluent programming
         */
        public Builder bucketName(final String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        /**
         * Set the host name or IP address of the BucketFS service.
         *
         * @param host host name or IP address of the BucketFS service
         * @return Builder instance for fluent programming
         */
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * Set the port the BucketFS service listens on.
         *
         * @param port HTTP or HTTPS port the BucketFS service listens on
         * @return Builder instance for fluent programming
         */
        public Builder port(final int port) {
            this.port = port;
            return this;
        }

        public ListingProvider build() {
            return new ListingProvider(this);
        }
    }
}
