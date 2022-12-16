package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.net.http.HttpClient;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.testutil.BucketCreator;

@Tag("slow")
class ListingProviderIT extends AbstractBucketIT {
    private static HttpClient HTTP_CLIENT = new HttpClientBuilder() //
            // .certificate(null)
            // .raiseTlsErrors(false)
            .build();

    @Test
    void testListBuckets() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        bucketCreator.command().execute();

        bucketCreator.waitUntilBucketExists();

        final List<String> actual = listBuckets();
        assertThat(actual, hasItems(bucketCreator.getBucketName(), "default"));
    }

    @Test
    void listBucketContentsWithoutReadPassword() throws Exception {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        bucketCreator.command().execute();

        final SyncAwareBucket bucket = bucketCreator.waitUntilBucketExists();

        final var path = "folder/file.txt";
        bucket.uploadStringContent("file content", path);
        final List<String> listing = listContents(bucket.getBucketName(), "folder");
        assertThat(listing, hasItem(path));
    }

    private List<String> listBuckets() throws BucketAccessException {
        return listContents(null, "");
    }

    private List<String> listContents(final String bucketName, final String path) throws BucketAccessException {
        return ListingProvider.builder() //
                .httpClient(HTTP_CLIENT) //
                .bucketName(bucketName) //
                .host(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .build() //
                .listContents(path);
    }

    private BucketCreator bucketCreator() {
        return new BucketCreator(ListingProviderIT.class, EXASOL);
    }
}
