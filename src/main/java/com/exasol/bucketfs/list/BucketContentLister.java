package com.exasol.bucketfs.list;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.bucketfs.list.ListingRetriever.removeLeadingSeparator;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.exasol.bucketfs.BucketAccessException;

/**
 * This class enables to list the contents of a directory in a bucket.
 */
public class BucketContentLister {
    private final URI bucketUri;
    private final ListingRetriever listingRetriever;
    private final String readPassword;

    /**
     * Create a new instance of {@link BucketContentLister}.
     *
     * @param bucketUri        URI of bucket to list contents for
     * @param listingRetriever used to retrieve the raw listing
     * @param readPassword     password for reading non-public buckets, not relevant for public buckets
     */
    public BucketContentLister(final URI bucketUri, final ListingRetriever listingRetriever,
            final String readPassword) {
        this.bucketUri = bucketUri;
        this.listingRetriever = listingRetriever;
        this.readPassword = readPassword;
    }

    /**
     * Retrieve the listing.
     *
     * @param path      path to list the contents of
     * @param recursive {@code true} if result should include entries in subdirectories of the specified path, too
     * @return list of files and subdirectories
     * @throws BucketAccessException in case of errors during retrieval of the inventory
     */
    public List<String> retrieve(final String path, final boolean recursive) throws BucketAccessException {
        final String prefix = removeLeadingSeparator(path);
        final List<String> list = this.listingRetriever.retrieve(this.bucketUri, this.readPassword) //
                .filter(e -> e.startsWith(prefix)) // should include "prefix" and "prefix/"
                .map(e -> removeLeadingSeparator(e.substring(prefix.length()))) // cut of path prefix
                .map(recursive ? Function.identity() : this::extractFirstPathComponent) //
                .distinct() //
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            throw pathToBeListedNotFoundException(path);
        } else {
            return list;
        }
    }

    private String extractFirstPathComponent(final String path) {
        final int i = path.indexOf(PATH_SEPARATOR);
        return i < 0 ? path : path.substring(0, i + 1);
    }

    private BucketAccessException pathToBeListedNotFoundException(final String path) {
        return new BucketAccessException(messageBuilder("E-BFSJ-11")
                .message("Unable to list contents of {{path}} in bucket {{bucket}}: No such file or directory.", //
                        path, this.bucketUri)
                .toString());
    }
}
