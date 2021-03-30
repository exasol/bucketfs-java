package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.Container.ExecResult;

import com.exasol.config.BucketConfiguration;
import com.exasol.containers.exec.ExitCode;

@Tag("slow")
class SyncAwareBucketIT extends AbstractBucketIT {
    private Bucket getDefaultBucket() {
        final BucketConfiguration bucketConfiguration = getDefaultBucketConfiguration();
        return SyncAwareBucket.builder()//
                .ipAddress(getContainerIpAddress()) //
                .httpPort(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .monitor(createBucketMonitor()) //
                .build();
    }

    // [itest->dsn~uploading-to-bucket~1]
    @Test
    void testUploadFile(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String fileName = "test-uploaded.txt";
        final Path testFile = createTestFile(tempDir, fileName, 10000);
        final Bucket bucket = getDefaultBucket();
        bucket.uploadFile(testFile, fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    private Path createTestFile(final Path tempDir, final String fileName, final int sizeInKiB) throws IOException {
        final RandomFileGenerator generator = new RandomFileGenerator();
        final Path path = tempDir.resolve(Path.of(fileName));
        generator.createRandomFile(path, sizeInKiB);
        return path;
    }

    // [itest->dsn~uploading-to-bucket~1]
    @ValueSource(strings = { "dir1/", "dir2/sub2/", "dir3/sub3/subsub3/", "/dir4/", "/dir5/sub5/" })
    @ParameterizedTest
    void testUploadToDirectoryInBucket(final String pathInBucket, @TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException, TimeoutException {
        final String fileName = "file.txt";
        final Path file = createTestFile(tempDir, fileName, 1);
        final Bucket bucket = getDefaultBucket();
        bucket.uploadFile(file, pathInBucket);
        assertThat(getDefaultBucket().listContents(pathInBucket), contains(fileName));
    }

    // [itest->dsn~uploading-strings-to-bucket~1]
    @Test
    void testUploadStringContent() throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = getDefaultBucket();
        bucket.uploadStringContent(content, pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket.toString()));
    }

    // [itest->dsn~uploading-input-stream-to-bucket~1]
    @Test
    void testUploadInputStreamContent() throws BucketAccessException, InterruptedException, TimeoutException {
        final String content = "Hello BucketFS!";
        final String pathInBucket = "string-uploaded.txt";
        final Bucket bucket = getDefaultBucket();
        bucket.uploadInputStream(() -> new ByteArrayInputStream(content.getBytes()), pathInBucket);
        assertThat(bucket.listContents(), hasItem(pathInBucket));
    }

    @Test
    void testUploadNonExistentFileThrowsException() {
        final Path file = Path.of("/this/path/does/not/exist");
        assertThrows(BucketAccessException.class, () -> getDefaultBucket().uploadFile(file, "nowhere.txt"));
    }

    @Test
    void testUploadFileToIllegalUrlThrowsException(@TempDir final Path tempDir) throws IOException {
        final Path file = createTestFile(tempDir, "irrelevant.txt", 1);
        assertThrows(BucketAccessException.class,
                () -> getDefaultBucket().uploadFile(file, "this\\is\\an\\illegal\\URL"));
    }

    @Test
    void testUploadContentToIllegalUrlThrowsException() {
        assertThrows(BucketAccessException.class,
                () -> getDefaultBucket().uploadStringContent("irrelevant content", "this\\is\\an\\illegal\\URL"));
    }

    // [itest->dsn~downloading-a-file-from-a-bucket~1]
    @Test
    void testDownloadFile(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException, IOException {
        final String fileName = "read_me.txt";
        final Bucket bucket = getDefaultBucket();
        final String content = "read me";
        bucket.uploadStringContent(content, fileName);
        final Path pathToFile = tempDir.resolve(fileName);
        bucket.downloadFile(fileName, pathToFile);
        assertThat(Files.readString(pathToFile), equalTo(content));
    }

    @Test
    void testDownloadFileThrowsExceptionOnIllegalLocalPath(@TempDir final Path tempDir)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final Path pathToFile = tempDir.resolve("/this/path/does/not/exist");
        final String pathInBucket = "foo.txt";
        final Bucket bucket = getDefaultBucket();
        bucket.uploadStringContent("some content", pathInBucket);
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> bucket.downloadFile(pathInBucket, pathToFile));
        assertThat(exception.getCause(), instanceOf(IOException.class));
    }

    // [itest->dsn~waiting-until-file-appears-in-target-directory~1]
    // [itest->dsn~bucketfs-object-overwrite-throttle~1]
    @Test
    void testReplaceFile(@TempDir final Path tempDir) throws InterruptedException, BucketAccessException,
            TimeoutException, IOException, NoSuchAlgorithmException {
        final int scaleContentSizeBy = 10000000;
        final String fileName = "replace_me.txt";
        final String absolutePathInContainer = "/exa/data/bucketfs/bfsdefault/.dest/default/" + fileName;
        final String contentA = "0123456789\n";
        final Path fileA = Files.writeString(tempDir.resolve("a.txt"), contentA.repeat(scaleContentSizeBy));
        final String contentB = "abcdeABCDE\n";
        final Path fileB = Files.writeString(tempDir.resolve("b.txt"), contentB.repeat(scaleContentSizeBy));
        final Bucket bucket = getDefaultBucket();
        for (int i = 1; i <= 10; ++i) {
            final boolean useA = (i % 2) == 1;
            final Path currentFile = useA ? fileA : fileB;
            final String currentFirstLine = useA ? contentA : contentB;
            bucket.uploadFile(currentFile, fileName);
            final ExecResult execInContainer = EXASOL.execInContainer("head", "-n", "1", absolutePathInContainer);
            if (execInContainer.getExitCode() == ExitCode.OK) {
                assertThat("Upload number " + i + ": file " + (useA ? "A" : "B"), execInContainer.getStdout(),
                        equalTo(currentFirstLine));
            } else {
                fail("Unable to read hash from file in container");
            }
        }
    }
}