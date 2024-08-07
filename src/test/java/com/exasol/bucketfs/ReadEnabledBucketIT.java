package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.bucketfs.jsonrpc.CommandFactory;
import com.exasol.bucketfs.testutil.BucketCreator;
import com.exasol.bucketfs.testutil.ExceptionAssertions;

@Tag("slow")
class ReadEnabledBucketIT extends AbstractBucketIT {

    private static String uniqueFolderName;
    private static final String FOLDER_NAME = "folder.txt";
    private static final String FILE_NAME = "file.txt";

    @BeforeAll
    static void createTestFiles() throws InterruptedException, BucketAccessException, TimeoutException {
        uniqueFolderName = "ReadEnabledBucketIT-" + System.currentTimeMillis();
        final String fileName = uniqueFolderName + "/" + FOLDER_NAME + "/" + FILE_NAME;
        AbstractBucketIT.EXASOL.getDefaultBucket().uploadStringContent("content", fileName);
    }

    @Test
    void testGetDefaultBucket() {
        final var defaultBucket = getDefaultBucket();
        assertAll(() -> assertThat(defaultBucket.getBucketFsName(), equalTo(DEFAULT_BUCKETFS)),
                () -> assertThat(defaultBucket.getBucketName(), equalTo(DEFAULT_BUCKET)));
    }

    private ReadOnlyBucket getDefaultBucket() {
        return getDefaultBucket(getDefaultBucketConfiguration().getReadPassword());
    }

    private ReadOnlyBucket getDefaultBucket(final String readPassword) {
        return getBucket(DEFAULT_BUCKET, readPassword);
    }

    private ReadOnlyBucket getBucket(final String bucketName, final String readPassword) {
        return ReadEnabledBucket.builder() //
                .raiseTlsErrors(true) //
                .host(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .useTls(dbUsesTls()) //
                .certificate(getDbCertificate()) //
                .allowAlternativeHostName(getHost()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(bucketName) //
                .readPassword(readPassword) //
                .build();
    }

    // [itest->dsn~bucket-lists-its-contents~2]
    @Test
    void testListBucketContentsWithRootPath() throws BucketAccessException {
        assertThat(getDefaultBucket().listContents(), hasItem(uniqueFolderName + "/"));
    }

    // [itest->dsn~bucket-lists-its-contents-recursively~1]
    @Test
    void testListBucketContentsRecursivelyWithRootPath() throws BucketAccessException {
        assertThat(getDefaultBucket().listContentsRecursively(),
                hasItem(startsWith(uniqueFolderName + "/" + FOLDER_NAME)));
    }

    // [itest->dsn~bucket-lists-its-contents~2]
    @ValueSource(strings = { "/{folder}/", "{folder}/", "{folder}" })
    @ParameterizedTest
    void testListContents(final String pathInBucket) throws BucketAccessException {
        assertThat(getDefaultBucket().listContents(pathInBucket.replace("{folder}", uniqueFolderName)),
                hasItem(startsWith(FOLDER_NAME)));
    }

    // [itest->dsn~bucket-lists-its-contents-recursively~1]
    @ValueSource(strings = { "/recursive-dir1/", "recursive-dir1/", "recursive-dir1" })
    @ParameterizedTest
    void testListContentsRecursively(final String pathInBucket)
            throws BucketAccessException, InterruptedException, TimeoutException {
        getDefaultBucketForWriting().uploadStringContent("dummy-content", "recursive-dir1/dir2/file.txt");
        assertThat(getDefaultBucket().listContentsRecursively(pathInBucket), contains("dir2/file.txt"));
    }

    @Test
    void testListContentsWithWrongReadPasswordFails() throws Exception {
        final String bucketName = createBucket("dir/file.txt", "protected content");
        final ReadOnlyBucket bucket = getBucket(bucketName, "wrong read password");
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class, () -> bucket.listContents("dir/"), //
                startsWith("E-BFSJ-3: Access denied trying to list "));
    }

    private String createBucket(final String pathInBucket, final String content)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final BucketCreator bucketCreator = new BucketCreator(ReadEnabledBucketIT.class, EXASOL) //
                .assumeJsonRpcAvailable();
        final CommandFactory factory = bucketCreator.createCommandFactory(false);
        bucketCreator.createBucket(false, factory);
        final SyncAwareBucket bucket = bucketCreator.waitUntilBucketExists();
        bucket.uploadStringContent(content, pathInBucket);
        return bucketCreator.getBucketName();
    }

    @Test
    void testDownloadWithWrongReadPasswordFails() throws Exception {
        final String pathInBucket = "dir/file.txt";
        final String bucketName = createBucket(pathInBucket, "protected content");
        final ReadOnlyBucket bucket = getBucket(bucketName, "wrong read password");
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class,
                () -> bucket.downloadFileAsString(pathInBucket), //
                startsWith("E-BFSJ-3: Access denied trying to download "));
    }

    @Test
    void testListingBucketContentsOfIllegalPathThrowsException() {
        final var nonExistentPath = "illegal%path";
        final String protocol = dbUsesTls() ? "https" : "http";
        final String expected = String.format("E-BFSJ-11: Unable to list contents" //
                + " of '%s' in bucket '%s://%s:%s/%s/': No such file or directory.", //
                nonExistentPath, protocol, getHost(), getMappedDefaultBucketFsPort(), "default");
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class,
                () -> getDefaultBucket().listContents(nonExistentPath), //
                expected);
    }

    @Test
    void testDownloadFromIllegalPathThrowsException(@TempDir final Path tempDir) {
        final var pathToFile = tempDir.resolve("irrelevant");
        final var pathInBucket = "this/path/does/not/exist";
        final var bucket = getDefaultBucket();
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile), //
                matchesPattern("E-BFSJ-2: File or directory not found trying to download 'https?://.*/" + pathInBucket
                        + "'."));
    }

    @Test
    void testDeprecatedHttpPortBuilderMethodWorks() throws BucketAccessException {
        @SuppressWarnings("deprecation")
        final ReadOnlyBucket bucket = ReadEnabledBucket.builder() //
                .raiseTlsErrors(true) //
                .host(getHost()) //
                .httpPort(getMappedDefaultBucketFsPort()) //
                .useTls(dbUsesTls()) //
                .certificate(getDbCertificate()) //
                .allowAlternativeHostName(getHost()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(getDefaultBucketConfiguration().getReadPassword()) //
                .build();
        assertThat(bucket.listContents(), hasItem(uniqueFolderName + "/"));
    }
}
