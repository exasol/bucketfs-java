package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Note that some of the download integration tests are conducted through the write-enabled version of the bucket.
// This allows uploading the expected file first. Otherwise the test would contain too much duplication.
@Tag("slow")
class ReadEnabledBucketIT extends AbstractBucketIT {
    private ReadOnlyBucket getDefaultBucket() {
        final var bucketConfiguration = getDefaultBucketConfiguration();
        return ReadEnabledBucket.builder()//
                .ipAddress(getContainerIpAddress()) //
                .httpPort(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .build();
    }

    @Test
    void testGetDefaultBucket() {
        final var defaultBucket = getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(DEFAULT_BUCKET)));
    }

    // [itest->dsn~bucket-lists-its-contents~1]
    @Test
    void testListBucketContentsWithRootPath() throws BucketAccessException {
        assertThat(getDefaultBucket().listContents(), hasItem("EXAClusterOS"));
    }

    // [itest->dsn~bucket-lists-its-contents~1]
    @ValueSource(strings = { "EXAClusterOS/", "/EXAClusterOS/" })
    @ParameterizedTest
    void testListBucketContents(final String pathInBucket) throws BucketAccessException {
        assertThat(getDefaultBucket().listContents(pathInBucket), hasItem(startsWith("ScriptLanguages")));
    }

    @Test
    void testListBucketContentsOfIllegalPathThrowsException() {
        final var nonExistentPath = "illegal%path";
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> getDefaultBucket().listContents(nonExistentPath));
        assertThat(exception.getMessage(), matchesPattern("E-BFSJ-11: Unable to list contents of '" + nonExistentPath
                + "' in bucket bfsdefault/default: No such file or directory."));
    }

    @Test
    void testDownloadFileThrowsExceptionOnIllegalPathInBucket(@TempDir final Path tempDir) {
        final var pathToFile = tempDir.resolve("irrelevant");
        final var pathInBucket = "this/path/does/not/exist";
        final var bucket = getDefaultBucket();
        final var exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile));
        assertThat(exception.getMessage(),
                equalTo("E-BFSJ-2: File or directory not found trying to download http://.*/" + pathInBucket + "."));
    }
}