package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.net.http.HttpClient;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.list.BucketContentListing;
import com.exasol.bucketfs.list.BucketListing;
import com.exasol.bucketfs.testutil.BucketCreator;

@Tag("slow")
class ListingProviderIT extends AbstractBucketIT {

    private static HttpClient HTTP_CLIENT = new HttpClientBuilder().build();

    @Test
    void testListBuckets() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable().createBucket();

        bucketCreator.waitUntilBucketExists();
        final List<String> actual = listBuckets();
        assertThat(actual, hasItems(bucketCreator.getBucketName(), "default"));
    }

    @Test
    void listBucketContentsWithoutReadPassword() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable().createBucket();

        final SyncAwareBucket bucket = bucketCreator.waitUntilBucketExists();
        bucket.uploadStringContent("file content", "folder/file.txt");
        final List<String> listing = listContents(bucket.getBucketName(), "folder");
        assertThat(listing, hasItem("file.txt"));
    }

    private List<String> listBuckets() throws BucketAccessException {
        return new BucketListing(HTTP_CLIENT, "http", getHost(), getMappedDefaultBucketFsPort()).retrieve();
    }

    private List<String> listContents(final String bucketName, final String path) throws BucketAccessException {
        return new BucketContentListing(HTTP_CLIENT, "http", getHost(), getMappedDefaultBucketFsPort(), bucketName, "")
                .retrieve(path, false);
    }

    private BucketCreator bucketCreator() {
        return new BucketCreator(ListingProviderIT.class, EXASOL);
    }
}
