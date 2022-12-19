package com.exasol.bucketfs.list;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.bucketfs.BucketAccessException;

/**
 * This class enables to retrieve a list of buckets.
 */
public class BucketListing extends ListingProvider {

    /**
     * Create a new instance
     *
     * @param httpClient HTTP client to use
     * @param protocol   protocol to use: either "http:" or "https:"
     * @param host       host name or IP address
     * @param port       port of BucketFS service
     */
    public BucketListing(final HttpClient httpClient, final String protocol, final String host, final int port) {
        super(httpClient, protocol, host, port);
    }

    public List<String> retrieve() throws BucketAccessException {
        final List<String> list = listingStream(createPublicReadURI(), "").collect(Collectors.toList());
        if (list.isEmpty()) {
            throw pathToBeListedNotFoundException();
        } else {
            return list;
        }
    }

    private URI createPublicReadURI() {
        return super.createPublicReadURI("");
    }

    private BucketAccessException pathToBeListedNotFoundException() {
        return new BucketAccessException(messageBuilder("E-BFSJ-30") //
                .message("Unable to list buckets of {{bucket}}: No such file or directory.", createPublicReadURI()) //
                .toString());
    }
}
