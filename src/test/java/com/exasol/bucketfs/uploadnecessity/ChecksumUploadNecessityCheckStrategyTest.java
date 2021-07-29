package com.exasol.bucketfs.uploadnecessity;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.exasol.bucketfs.*;

//[itest->dsn~conditional-upload~1]
class ChecksumUploadNecessityCheckStrategyTest extends AbstractBucketIT {
    private static Connection connection;
    private Bucket bucket;
    private ChecksumUploadNecessityCheckStrategy uploadCheck;

    @BeforeAll
    static void beforeAll() throws SQLException {
        connection = EXASOL.createConnection();
    }

    @AfterAll
    static void afterAll() throws SQLException {
        connection.close();
    }

    private SyncAwareBucket getBucket() {
        final var bucketConfiguration = getDefaultBucketConfiguration();
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

    @BeforeEach
    void beforeEach() {
        this.bucket = getBucket();
        this.uploadCheck = new ChecksumUploadNecessityCheckStrategy(connection);
    }

    @Test
    void testGetSha512Checksum()
            throws NoSuchAlgorithmException, BucketAccessException, InterruptedException, TimeoutException {
        final String testContent = "test";
        final String testFile = "aFileForChecksumTest.txt";
        this.bucket.uploadStringContent(testContent, testFile);
        final String checksum = this.uploadCheck.getSha512Checksum(testFile, this.bucket);
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(testContent.getBytes());
        assertThat(checksum, equalTo(toHex(md.digest())));
    }

    @Test
    void testGetChecksumOfNonExistingFile() {
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> this.uploadCheck.getSha512Checksum(getUniqueFileName(), getBucket()));
        assertThat(exception.getMessage(), startsWith("F-BFSJ-15: Failed to determine checksum of file"));
    }

    private String toHex(final byte[] checksum) {
        final StringBuilder sb = new StringBuilder();
        for (final byte checksumByte : checksum) {
            sb.append(String.format("%02x", checksumByte));
        }
        return sb.toString();
    }

    @Test
    void testUploadNotRequired(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final String twoMbString = "a".repeat(2000000);
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, twoMbString);
        this.bucket.uploadFile(testFile, fileName);
        assertFalse(this.uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    void testUploadRequiredByDifferentContent(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException, InterruptedException {
        final String twoMbString = "a".repeat(2000000);
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, twoMbString);
        this.bucket.uploadStringContent("other", fileName);
        assertTrue(this.uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    void testUploadRequiredByNewFile(@TempDir final Path tempDir) throws IOException, BucketAccessException {
        final String twoMbString = "a".repeat(2000000);
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, twoMbString);
        assertTrue(this.uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    void testUploadRequiredBySmallFile(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final String smallString = "test";
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, smallString);
        this.bucket.uploadFile(testFile, fileName);
        assertTrue(this.uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    void testIsUploadNecessaryWithNonExistingFile(@TempDir final Path tempDir) {
        final BucketAccessException exception = assertThrows(BucketAccessException.class, () -> this.uploadCheck
                .isUploadNecessary(tempDir.resolve("nonExistingFile.txt"), "nonExistingFile.txt", getBucket()));
        assertThat(exception.getMessage(),
                equalTo("E-BFSJ-17: Failed to check if we need to upload 'nonExistingFile.txt'."));
    }

    private String getUniqueFileName() {
        return "myFile-" + System.currentTimeMillis() + ".txt";
    }
}