package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.list.*;
import com.exasol.bucketfs.testutil.BucketCreator;

@Tag("slow")
class ListingRetrieverIT extends AbstractBucketIT {

    private static final HttpClient HTTP_CLIENT = new HttpClientBuilder().build();
    private static final ListingRetriever CONTENT_LISTER = new ListingRetriever(HTTP_CLIENT);

    @Test
    void testListBuckets() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable().createBucket();
        bucketCreator.waitUntilBucketExists();
        final List<String> actual = listBuckets();
        assertThat(actual, hasItems(bucketCreator.getBucketName(), "default"));
    }

    private List<String> listBuckets() throws BucketAccessException {
        return new BucketService(uri(""), CONTENT_LISTER).retrieve();
    }

    private URI uri(final String suffix) {
        final String protocol = dbUsesTls() ? "https" : "http";
        return ListingRetriever.publicReadUri(protocol, getHost(), getMappedDefaultBucketFsPort(), suffix);
    }

    @Test
    void testListBucketContentsWithoutReadPassword() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable().createBucket();
        final SyncAwareBucket bucket = bucketCreator.waitUntilBucketExists();
        bucket.uploadStringContent("file content", "folder/file.txt");
        final List<String> listing = listContents(bucket.getBucketName(), "folder");
        assertThat(listing, hasItem("file.txt"));
    }

    private List<String> listContents(final String bucketName, final String path) throws BucketAccessException {
        return new BucketContentLister(uri(bucketName), CONTENT_LISTER, "").retrieve(path, false);
    }

    private BucketCreator bucketCreator() {
        return new BucketCreator(ListingRetrieverIT.class, EXASOL);
    }
}
