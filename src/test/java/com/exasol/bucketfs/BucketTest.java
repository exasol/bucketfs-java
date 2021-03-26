package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.time.Instant;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class BucketTest {
    @Test
    void testIsObjectSynchronizedThrowsExceptionIfNoMonitorIsAvailable() {
        final Bucket bucket = WriteEnabledBucket.builder() //
                .serviceName("the_service") //
                .name("the_bucket") //
                .ipAddress("localhost") //
                .httpPort(1234) //
                .build();
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.isObjectSynchronized("the_path", Instant.now()));
        assertThat(exception.getMessage(), Matchers.containsString(
                "Unable to determine whether \"the_path\" in \"the_service/the_bucket\" is synchronized."));
    }
}