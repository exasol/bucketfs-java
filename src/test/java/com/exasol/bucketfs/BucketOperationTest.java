package com.exasol.bucketfs;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BucketOperationTest {
    @CsvSource({ "DOWNLOAD, download", "LIST, list", "UPLOAD, upload" })
    @ParameterizedTest
    void testToString(final BucketOperation operation, final String expectedOutput) {
        MatcherAssert.assertThat(operation.toString(), Matchers.equalTo(expectedOutput));
    }
}