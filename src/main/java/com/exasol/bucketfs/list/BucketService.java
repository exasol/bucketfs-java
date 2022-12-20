package com.exasol.bucketfs.list;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.bucketfs.BucketAccessException;

/**
 * This class enables to retrieve a list of buckets.
 */
public class BucketService {
    private final ListingRetriever listingRetriever;
    private final URI bucketServiceUri;

    /**
     * Create a new instance of {@link BucketService}.
     *
     * @param bucketServiceUri URI to access the bucket service
     * @param listingRetriever used to retrieve the raw listing
     */
    public BucketService(final URI bucketServiceUri, final ListingRetriever listingRetriever) {
        this.bucketServiceUri = bucketServiceUri;
        this.listingRetriever = listingRetriever;
    }

    /**
     * @return list of children of the current bucket
     * @throws BucketAccessException in case of failure
     */
    public List<String> retrieve() throws BucketAccessException {
        final List<String> list = this.listingRetriever //
                .retrieve(this.bucketServiceUri, "") //
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            throw pathToBeListedNotFoundException();
        } else {
            return list;
        }
    }

    private BucketAccessException pathToBeListedNotFoundException() {
        return new BucketAccessException(messageBuilder("E-BFSJ-30") //
                .message("Unable to list buckets of {{bucket}}: No such file or directory.", this.bucketServiceUri) //
                .toString());
    }
}
