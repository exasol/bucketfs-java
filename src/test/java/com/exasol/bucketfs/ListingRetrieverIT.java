package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.list.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("slow")
class ListingRetrieverIT extends AbstractBucketIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListingRetrieverIT.class);

    @Test
    void testListBuckets() throws Exception {
        final TemporaryBucketFactory bucketFactory = new TemporaryBucketFactory(EXASOL);
        final Bucket temporaryBucket = bucketFactory.createPublicBucket();
        final ListingRetriever contentLister = new ListingRetriever(temporaryBucket.getHttpClient());
        final BucketService service = new BucketService(uri(""), contentLister);
        final List<String> bucketListing = service.retrieve();
        assertThat(bucketListing, hasItems(temporaryBucket.getBucketName(), "default"));
    }

    private URI uri(final String suffix) {
        final String protocol = dbUsesTls() ? "https" : "http";
        LOGGER.info("Using protocol {} for listing URI", protocol);
        return ListingRetriever.publicReadUri(protocol, getHost(), getMappedDefaultBucketFsPort(), suffix);
    }

    @Test
    void testListBucketContentsWithoutReadPassword() throws Exception {
        final TemporaryBucketFactory bucketFactory = new TemporaryBucketFactory(EXASOL);
        final Bucket temporaryBucket = bucketFactory.createPublicBucket();
        temporaryBucket.uploadStringContent("file content", "folder/file.txt");
        final ListingRetriever contentLister = new ListingRetriever(temporaryBucket.getHttpClient());
        final String bucketName = temporaryBucket.getBucketName();
        final List<String> listing = new BucketContentLister(uri(bucketName), contentLister, "")
                .retrieve("folder", false);
        assertThat(listing, hasItem("file.txt"));
    }
}
