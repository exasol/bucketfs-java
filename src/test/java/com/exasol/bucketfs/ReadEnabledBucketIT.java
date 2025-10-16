package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static com.exasol.bucketfs.BucketObjectReplicator.copyBucket;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

@Tag("slow")
class ReadEnabledBucketIT extends AbstractBucketIT {
    private static TemporaryBucketFactory temporaryBucketFactory;

    @BeforeAll
    static void beforeAll() {
        temporaryBucketFactory = new TemporaryBucketFactory(EXASOL);
    }

    @Test
    void testGetDefaultBucket() {
        final Bucket defaultBucket = EXASOL.getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(DEFAULT_BUCKET)));
    }

    // [itest->dsn~bucket-lists-its-contents~2]
    @Test
    void testListBucketContentsWithRootPath() throws BucketAccessException {
        final Bucket bucket = temporaryBucketFactory.createPublicBucket();
        uploadFileWithContentToPath(bucket, "some content", "foo/bar.txt");
        assertThat(bucket.listContents(), containsInAnyOrder("foo/"));
    }

    // [itest->dsn~bucket-lists-its-contents-recursively~1]
    @Test
    void testListBucketContentsRecursivelyWithRootPath() throws BucketAccessException {
        final Bucket bucket = temporaryBucketFactory.createPublicBucket();
        uploadFileWithContentToPath(bucket, "some content", "foo/bar.txt");
        assertThat(bucket.listContentsRecursively(), containsInAnyOrder("foo/bar.txt"));
    }

    // [itest->dsn~bucket-lists-its-contents~2]
    @ValueSource(strings = { "/folder/", "folder/", "folder" })
    @ParameterizedTest
    void testListContentsForPath(final String pathInBucket) throws BucketAccessException {
        final Bucket bucket = temporaryBucketFactory.createPublicBucket();
        uploadFileWithContentToPath(bucket, "some content", "folder/file.txt");
        assertThat(bucket.listContents(pathInBucket), containsInAnyOrder("file.txt"));
    }

    // [itest->dsn~bucket-lists-its-contents-recursively~1]
    @ValueSource(strings = { "/recursive-dir1/", "recursive-dir1/", "recursive-dir1" })
    @ParameterizedTest
    void testListContentOsRecursively(final String pathInBucket) throws BucketAccessException {
        final Bucket bucket = temporaryBucketFactory.createPublicBucket();
        uploadFileWithContentToPath(bucket, "some content", "recursive-dir1/dir2/file.txt");
        assertThat(bucket.listContentsRecursively(pathInBucket), containsInAnyOrder("dir2/file.txt"));
    }

    @Test
    void testListContentsWithWrongReadPasswordFails() {
        final Bucket bucket = temporaryBucketFactory.createPrivateBucket();
        uploadFileWithContentToPath(bucket, "protected content", "dir/file.txt");
        final ReadOnlyBucket misconfiguredBucket = copyBucket(bucket)
                .readPassword("wrong password")
                .build();
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> misconfiguredBucket.listContents("dir/"));
        assertThat(exception.getMessage(), startsWith("E-BFSJ-3: Access denied trying to list "));
    }

    @Test
    void testDownloadWithWrongReadPasswordFails() {
        final Bucket bucket = temporaryBucketFactory.createPrivateBucket();
        uploadFileWithContentToPath(bucket, "protected content", "dir/file.txt");
        final ReadOnlyBucket misconfiguredBucket = copyBucket(bucket)
                .readPassword("wrong password")
                .build();
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> misconfiguredBucket.downloadFileAsString("dir/file.txt"));
        assertThat(exception.getMessage(), startsWith("E-BFSJ-3: Access denied trying to download "));
    }

    @Test
    void testListingBucketContentsOfIllegalPathThrowsException() {
        final Bucket bucket = EXASOL.getDefaultBucket();
        final String nonExistentPath = "illegal%path";
        final String protocol = dbUsesTls() ? "https" : "http";
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.listContents(nonExistentPath));
        final String expectedErrorMessage = String.format("E-BFSJ-11: Unable to list contents" //
                + " of '%s' in bucket '%s://%s:%s/%s/': No such file or directory.", //
                nonExistentPath, protocol, getHost(), getMappedDefaultBucketFsPort(), bucket.getBucketName());
        assertThat(exception.getMessage(), equalTo(expectedErrorMessage));
    }

    @Test
    void testDownloadFromIllegalPathThrowsException(@TempDir final Path tempDir) {
        final Bucket bucket = EXASOL.getDefaultBucket();
        final String nonExistentPath = "this/path/does/not/exist";
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(nonExistentPath, tempDir.resolve("irrelevant")));
        assertThat(exception.getMessage(),
                matchesPattern(
                        "E-BFSJ-2: File or directory not found trying to download 'https?://.*/"
                                + nonExistentPath + "'."));
    }
}
