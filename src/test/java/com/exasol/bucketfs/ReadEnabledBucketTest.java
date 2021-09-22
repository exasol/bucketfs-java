package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReadEnabledBucketTest {
    @CsvSource({ //
            "1234, service, bucket, service/bucket", //
            "1234,        , bucket, 1234:bucket" //
    })
    @ParameterizedTest
    void toString(final int port, final String serviceName, final String bucketName, final String expectedOutput) {
        final var bucket = ReadEnabledBucket.builder() //
                .ipAddress("192.168.1.1") //
                .httpPort(port) //
                .serviceName(serviceName) //
                .name(bucketName).build();
        assertThat(bucket.toString(), equalTo(expectedOutput));
    }

    @Test
    void testGetFullyQualifiedBucketName() {
        final var bucket = ReadEnabledBucket.builder().serviceName("service").name("bucket").build();

        assertThat(bucket.getFullyQualifiedBucketName(), equalTo("service/bucket"));
    }
}