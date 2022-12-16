package com.exasol.bucketfs.list;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.function.Predicate;

import com.exasol.bucketfs.BucketAccessException;

/**
 * This class enables to list the contents of a directory in a bucket.
 */
public class BucketContentListing extends ListingProvider {

    private final String bucketName;

    /**
     * Create a new instance
     *
     * @param httpClient HTTP client to use
     * @param protocol   protocol to use: either "http:" or "https:"
     * @param host       host name or IP address
     * @param port       port of BucketFS service
     * @param bucketName name of the bucket to list the contents for
     */
    public BucketContentListing(final HttpClient httpClient, final String protocol, final String host, final int port,
            final String bucketName) {
        super(httpClient, protocol, host, port);
        this.bucketName = bucketName;
    }

    /**
     * @return list of contents on root level of the current bucket
     * @throws BucketAccessException in case of errors during retrieval of the inventory
     */
    public List<String> retrieve() throws BucketAccessException {
        return retrieve();
    }

    /**
     *
     * @param path path to list the contents of
     * @return list of files and subdirectories contained in the given path of he current bucket
     * @throws BucketAccessException in case of errors during retrieval of the inventory
     */
    public List<String> retrieve(final String path) throws BucketAccessException {
        final var uri = createPublicReadURI();
        final List<String> list = requestListing(uri, getFilter(path));
        if (list.isEmpty()) {
            throw pathToBeListedNotFoundException(path);
        } else {
            return list;
        }
    }

    private Predicate<String> getFilter(final String path) {
        return e -> e.startsWith(removeLeadingSeparator(path));
    }

    private URI createPublicReadURI() {
        return super.createPublicReadURI(this.bucketName);
    }

    private BucketAccessException pathToBeListedNotFoundException(final String path) {
        return new BucketAccessException(messageBuilder("E-BFSJ-11")
                .message("Unable to list contents of {{path}} in bucket {{bucket}}: No such file or directory.", //
                        path, createPublicReadURI())
                .toString());
    }
}
