package com.exasol.bucketfs.list;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.bucketfs.BucketOperation.LIST;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.bucketfs.*;

/**
 * This class enables to request list of buckets or of objects inside a bucket.
 */
public abstract class ListingProvider {

    private static final Logger LOGGER = Logger.getLogger(ListingProvider.class.getName());
    private final HttpClient httpClient;
    private final String protocol;
    private final String host;
    private final int port;

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

    protected List<String> requestListing(final URI uri, final Predicate<String> filter) throws BucketAccessException {
        return Arrays.stream(requestListing(uri).split("\\s+")) //
                .sorted() //
                .filter(filter) //
                .collect(Collectors.toList());
    }

    private String requestListing(final URI uri) throws BucketAccessException {
        LOGGER.fine(() -> "Listing contents of URI '" + uri + "'");
        try {
            final var request = HttpRequest.newBuilder(uri).build();
            final var response = this.httpClient.send(request, BodyHandlers.ofString());
            HttpResponseEvaluator.evaluate(uri, LIST, response.statusCode());
            return response.body();
        } catch (final IOException exception) {
            throw BucketAccessException.downloadIoException(uri, LIST, exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw BucketAccessException.downloadInterruptedException(uri, LIST);
        }
    }

    protected URI createPublicReadURI(final String suffix) {
        return URI.create(this.protocol + "://" + this.host + ":" + this.port + "/" + suffix);
    }
}
