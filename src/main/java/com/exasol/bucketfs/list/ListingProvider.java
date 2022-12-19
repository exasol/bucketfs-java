package com.exasol.bucketfs.list;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.bucketfs.BucketOperation.LIST;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.exasol.bucketfs.*;

/**
 * This class enables to request list of buckets or of objects inside a bucket.
 */
public abstract class AbstractListingProvider {

    private static final Logger LOGGER = Logger.getLogger(ListingProvider.class.getName());
    private final HttpClient httpClient;
    private final String protocol;
    private final String host;
    private final int port;

    /**
     * Create a new instance
     *
     * @param httpClient HTTP client to use
     * @param protocol   protocol to use: either "http" or "https"
     * @param host       host name or IP address
     * @param port       port of BucketFS service
     */
    protected ListingProvider(final HttpClient httpClient, final String protocol, final String host, final int port) {
        this.httpClient = httpClient;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    /**
     * @param path remove leading {@link BucketConstants#PATH_SEPARATOR} from this path
     * @return path with potential leading {@link BucketConstants#PATH_SEPARATOR} removed
     */
    public static String removeLeadingSeparator(final String path) {
        return path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path;
    }

    /**
     * Retrieve the content for the given URI as stream.
     *
     * @param uri          URI to retrieve listing for
     * @param readPassword read password, only relevant for non-public buckets
     * @return stream of strings
     * @throws BucketAccessException in case of failure
     */
    protected Stream<String> listingStream(final URI uri, final String readPassword) throws BucketAccessException {
        return Arrays.stream(requestListing(uri, readPassword).split("\\s+")).sorted();
    }

    private String requestListing(final URI uri, final String readPassword) throws BucketAccessException {
        LOGGER.fine(() -> "Listing contents of URI '" + uri + "'");
        try {
            final HttpRequest request = HttpRequest.newBuilder(uri) //
                    .header("Authorization", encodeBasicAuth(readPassword)) //
                    .build();
            final HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
            HttpResponseEvaluator.evaluate(uri, LIST, response.statusCode());
            return response.body();
        } catch (final IOException exception) {
            throw BucketAccessException.downloadIoException(uri, LIST, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw BucketAccessException.downloadInterruptedException(uri, LIST);
        }
    }

    private String encodeBasicAuth(final String readPassword) {
        return "Basic " + Base64.getEncoder().encodeToString(("r:" + readPassword).getBytes());
    }

    /**
     * @param suffix suffix to add to the URI
     * @return URI based on the fields of the current instance: protocol, host, port
     */
    protected URI createPublicReadURI(final String suffix) {
        return URI.create(this.protocol + "://" + this.host + ":" + this.port + "/" + suffix);
    }
}
