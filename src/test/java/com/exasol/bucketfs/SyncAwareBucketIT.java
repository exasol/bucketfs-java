package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.testutil.LogBasedBucketFsMonitor;
import com.exasol.bucketfs.uploadnecessity.UploadNecessityCheckStrategy;
import com.exasol.containers.exec.ExitCode;

@Tag("slow")
class SyncAwareBucketIT extends AbstractBucketIT {

    private SyncAwareBucket createDefaultBucket() {
        final var bucketConfiguration = getDefaultBucketConfiguration();
        final LogBasedBucketFsMonitor monitor = createBucketMonitor();
        return SyncAwareBucket.builder()//
                .ipAddress(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .monitor(monitor) //
                .stateRetriever(new TimestampRetriever()) //
                .build();
    }

    // [itest->dsn~uploading-to-bucket~1]
    @Test
    void testUploadFile(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final var fileName = "test-uploaded.txt";
        final var testFile = createTestFile(tempDir, fileName, 10000);
        final var bucket = createDefaultBucket();
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    private Path createTestFile(final Path tempDir, final String fileName, final int sizeInKiB) throws IOException {
        final var generator = new RandomFileGenerator();
        final var path = tempDir.resolve(Path.of(fileName));
        generator.createRandomFile(path, sizeInKiB);
        return path;
    }

    // [itest->dsn~uploading-to-bucket~1]
    @ValueSource(strings = { "dir1/", "dir2/sub2/", "dir3/sub3/subsub3/", "/dir4/", "/dir5/sub5/" })
    @ParameterizedTest
    void testUploadToDirectoryInBucket(final String pathInBucket, @TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException, TimeoutException {
        final var fileName = "file.txt";
        final var file = createTestFile(tempDir, fileName, 1);
        final var bucket = createDefaultBucket();
        bucket.uploadFile(file, pathInBucket);
        assertThat(createDefaultBucket().listContents(pathInBucket), Matchers.contains(fileName));
    }

    // [itest->dsn~uploading-strings-to-bucket~1]
    @Test
    void testUploadStringContent() throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final var content = "Hello BucketFS!";
        final var pathInBucket = "string-uploaded.txt";
        final var bucket = createDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket.toString()));
    }

    // [itest->dsn~uploading-input-stream-to-bucket~1]
    @Test
    void testUploadInputStreamContent() throws BucketAccessException, InterruptedException, TimeoutException {
        final var content = "Hello BucketFS!";
        final var pathInBucket = "string-uploaded.txt";
        final var bucket = createDefaultBucket();
        bucket.uploadInputStream(() -> new ByteArrayInputStream(content.getBytes()), pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket));
    }

    @Test
    void testUploadNonExistentFileThrowsException() {
        final var file = Path.of("/this/path/does/not/exist");
        assertThrows(FileNotFoundException.class, () -> createDefaultBucket().uploadFile(file, "nowhere.txt"));
    }

    @Test
    void testUploadFileToIllegalUrlThrowsException(@TempDir final Path tempDir) throws IOException {
        final var file = createTestFile(tempDir, "irrelevant.txt", 1);
        assertThrows(BucketAccessException.class,
                () -> createDefaultBucket().uploadFile(file, "this\\is\\an\\illegal\\URL"));
    }

    @Test
    void testUploadContentToIllegalUrlThrowsException() {
        assertThrows(BucketAccessException.class,
                () -> createDefaultBucket().uploadStringContent("irrelevant content", "this\\is\\an\\illegal\\URL"));
    }

    // [itest->dsn~downloading-a-file-from-a-bucket~1]
    @Test
    void testDownloadFile(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final var fileName = "read_me.txt";
        final var bucket = createDefaultBucket();
        final var content = "read me";
        bucket.uploadStringContent(content, fileName);
        final var pathToFile = tempDir.resolve(fileName);
        bucket.downloadFile(fileName, pathToFile);
        assertThat(Files.readString(pathToFile), equalTo(content));
    }

    // [itest->dsn~downloading-a-file-from-a-bucket-as-string~1]
    @Test
    void testDownloadAsString() throws InterruptedException, BucketAccessException, TimeoutException {
        final var fileName = "read_me.txt";
        final var bucket = createDefaultBucket();
        final var content = "read me";
        bucket.uploadStringContent(content, fileName);
        final var result = bucket.downloadFileAsString(fileName);
        assertThat(result, equalTo(content));
    }

    @Test
    void testDownloadFileThrowsExceptionOnIllegalLocalPath(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final var pathToFile = tempDir.resolve("/this/path/does/not/exist");
        final var pathInBucket = "foo.txt";
        final var bucket = createDefaultBucket();
        bucket.uploadStringContent("some content", pathInBucket);
        final var exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile));
        assertThat(exception.getCause(), instanceOf(IOException.class));
    }

    // [itest->dsn~waiting-until-file-appears-in-target-directory~1]
    // [itest->dsn~bucketfs-object-overwrite-throttle~1]
    @Test
    void testReplaceFile(@TempDir final Path tempDir) throws InterruptedException, BucketAccessException,
            TimeoutException, IOException, NoSuchAlgorithmException {
        final var scaleContentSizeBy = 10000000;
        final var fileName = "replace_me.txt";
        final var absolutePathInContainer = "/exa/data/bucketfs/bfsdefault/.dest/default/" + fileName;
        final var contentA = "0123456789\n";
        final var fileA = Files.writeString(tempDir.resolve("a.txt"), contentA.repeat(scaleContentSizeBy));
        final var contentB = "abcdeABCDE\n";
        final var fileB = Files.writeString(tempDir.resolve("b.txt"), contentB.repeat(scaleContentSizeBy));
        final var bucket = createDefaultBucket();
        for (int i = 1; i <= 10; ++i) {
            final var useA = (i % 2) == 1;
            final var currentFile = useA ? fileA : fileB;
            final var currentFirstLine = useA ? contentA : contentB;
            bucket.uploadFile(currentFile, fileName);
            final var execInContainer = EXASOL.execInContainer("head", "-n", "1", absolutePathInContainer);
            if (execInContainer.getExitCode() == ExitCode.OK) {
                assertThat("Upload number " + i + ": file " + (useA ? "A" : "B"), execInContainer.getStdout(),
                        equalTo(currentFirstLine));
            } else {
                fail("Unable to read hash from file in container");
            }
        }
    }

    @Test
    // [itest->dsn~delete-a-file-from-a-bucket~1]
    void testDeleteFile() throws BucketAccessException, InterruptedException, TimeoutException {
        final var bucket = createDefaultBucket();
        final String testFile = getUniqueFileName();
        bucket.uploadStringContent("test", testFile);
        bucket.deleteFileNonBlocking(testFile);
        assertThat(bucket.listContents(), not(hasItem(testFile)));
    }

    @Test
    void testInterruptedDuringDeleteFile()
            throws BucketAccessException, InterruptedException, TimeoutException, IOException {
        assertDeleteThrowsExceptionWhenClientThrows(new InterruptedException());
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void testIoExceptionDuringDeleteFile()
            throws BucketAccessException, InterruptedException, TimeoutException, IOException {
        assertDeleteThrowsExceptionWhenClientThrows(new IOException());
    }

    private void assertDeleteThrowsExceptionWhenClientThrows(final Exception exceptionThrownByHttpClient)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final var bucket = createDefaultBucket();
        final String testFile = getUniqueFileName();
        bucket.uploadStringContent("test", testFile);
        final SyncAwareBucket bucketSpy = getBucketWithExceptionThrowingHttpClient(exceptionThrownByHttpClient, bucket);
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucketSpy.deleteFileNonBlocking(testFile));
        assertThat(exception.getMessage(), Matchers.startsWith("E-BFSJ-12: Failed to delete"));
    }

    private SyncAwareBucket getBucketWithExceptionThrowingHttpClient(final Exception exceptionThrownByHttpClient,
            final SyncAwareBucket bucket) throws IOException, InterruptedException {
        final SyncAwareBucket bucketSpy = spy(bucket);
        final HttpClient client = mock(HttpClient.class);
        when(client.send(any(), any())).thenThrow(exceptionThrownByHttpClient);
        when(bucketSpy.getClient()).thenReturn(client);
        return bucketSpy;
    }

    @Test
    void testInterruptedDuringUploadFile()
            throws BucketAccessException, InterruptedException, TimeoutException, IOException {
        final BucketAccessException exception = assertUploadThrowsExceptionWhenClientThrows(new InterruptedException());
        assertAll(//
                () -> assertThat(exception.getMessage(), Matchers.startsWith("E-BFSJ-6: Interrupted trying to upload")),
                () -> assertTrue(Thread.currentThread().isInterrupted())//
        );
    }

    @Test
    void testIoExceptionDuringUploadFile()
            throws BucketAccessException, InterruptedException, TimeoutException, IOException {
        final BucketAccessException exception = assertUploadThrowsExceptionWhenClientThrows(new IOException());
        assertThat(exception.getMessage(), Matchers.startsWith("E-BFSJ-7: I/O error trying to upload"));
    }

    private BucketAccessException assertUploadThrowsExceptionWhenClientThrows(
            final Exception exceptionThrownByHttpClient)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final var bucket = createDefaultBucket();
        final String testFile = getUniqueFileName();
        bucket.uploadStringContent("test", testFile);
        final SyncAwareBucket bucketSpy = getBucketWithExceptionThrowingHttpClient(exceptionThrownByHttpClient, bucket);
        return assertThrows(BucketAccessException.class, () -> bucketSpy.uploadStringContent("test", testFile));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testUploadNecessityCheckStrategy(final boolean uploadNecessary, @TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final SyncAwareBucket bucket = createDefaultBucket();
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        final UploadNecessityCheckStrategy uploadNecessityCheckStrategy = createUploadNeverStrategyMock(bucket,
                fileName, testFile, uploadNecessary);
        Files.writeString(testFile, "some content");
        bucket.setUploadNecessityCheckStrategy(uploadNecessityCheckStrategy);
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents().contains(fileName), equalTo(uploadNecessary));
        verify(uploadNecessityCheckStrategy).isUploadNecessary(testFile, fileName, bucket);
    }

    private UploadNecessityCheckStrategy createUploadNeverStrategyMock(final SyncAwareBucket bucket,
            final String fileName, final Path testFile, final boolean uploadNecessary) throws BucketAccessException {
        final UploadNecessityCheckStrategy uploadNecessityCheckStrategy = mock(UploadNecessityCheckStrategy.class);
        when(uploadNecessityCheckStrategy.isUploadNecessary(testFile, fileName, bucket)).thenReturn(uploadNecessary);
        return uploadNecessityCheckStrategy;
    }

    private String getUniqueFileName() {
        return "myFile-" + System.currentTimeMillis() + ".txt";
    }
}