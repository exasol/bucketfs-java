package com.exasol.bucketfs.list;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.exasol.bucketfs.BucketAccessException;

/**
 * This class enables to list the contents of a directory in a bucket.
 */
public class BucketContentListing extends ListingProvider {

    private final String bucketName;
    private final String readPassword;

    /**
     * Create a new instance
     *
     * @param httpClient   HTTP client to use
     * @param protocol     protocol to use: either "http" or "https"
     * @param host         host name or IP address
     * @param port         port of BucketFS service
     * @param bucketName   name of the bucket to list the contents for
     * @param readPassword password for reading non-public buckets
     */
    public BucketContentListing(final HttpClient httpClient, final String protocol, final String host, final int port,
            final String bucketName, final String readPassword) {
        super(httpClient, protocol, host, port);
        this.bucketName = bucketName;
        this.readPassword = readPassword;
    }

    /**
     * Retrieve the listing.
     * @param path      path to list the contents of
     * @param recursive {@code true} if result should include entries in subdirectories of the specified path, too
     * @return list of files and subdirectories
     * @throws BucketAccessException in case of errors during retrieval of the inventory
     */
    public List<String> retrieve(final String path, final boolean recursive) throws BucketAccessException {
        final var uri = createPublicReadURI();
        final String prefix = removeLeadingSeparator(path);
        final List<String> list = listingStream(uri, this.readPassword) //
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

    private URI createPublicReadURI() {
        return super.createPublicReadURI(this.bucketName + "/");
    }

    private BucketAccessException pathToBeListedNotFoundException(final String path) {
        return new BucketAccessException(messageBuilder("E-BFSJ-11")
                .message("Unable to list contents of {{path}} in bucket {{bucket}}: No such file or directory.", //
                        path, createPublicReadURI())
                .toString());
    }
}
