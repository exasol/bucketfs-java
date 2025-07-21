package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.list.*;

@Tag("slow")
class ListingRetrieverIT extends AbstractBucketIT {
    private static final HttpClient HTTP_CLIENT = new HttpClientBuilder().build();
    private static final ListingRetriever CONTENT_LISTER = new ListingRetriever(HTTP_CLIENT);

    @Test
    void testListBuckets() throws Exception {
        final TemporaryBucketFactory bucketFactory = new TemporaryBucketFactory(EXASOL);
        final Bucket temporaryBucket = bucketFactory.createPublicBucket();
        final BucketService service = new BucketService(uri(""), CONTENT_LISTER);
        final List<String> bucketListing = service.retrieve();
        assertThat(bucketListing, hasItems(temporaryBucket.getBucketName(), "default"));
    }

    private URI uri(final String suffix) {
        final String protocol = dbUsesTls() ? "https" : "http";
        return ListingRetriever.publicReadUri(protocol, getHost(), getMappedDefaultBucketFsPort(), suffix);
    }

    @Test
    void testListBucketContentsWithoutReadPassword() throws Exception {
        final TemporaryBucketFactory bucketFactory = new TemporaryBucketFactory(EXASOL);
        final Bucket temporaryBucket = bucketFactory.createPublicBucket();
        temporaryBucket.uploadStringContent("file content", "folder/file.txt");
        final String bucketName = temporaryBucket.getBucketName();
        final List<String> listing = new BucketContentLister(uri(bucketName), CONTENT_LISTER, "")
                .retrieve("folder", false);
        assertThat(listing, hasItem("file.txt"));
    }
}
