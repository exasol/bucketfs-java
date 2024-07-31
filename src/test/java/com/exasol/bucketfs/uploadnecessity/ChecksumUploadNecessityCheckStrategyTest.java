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
import com.exasol.bucketfs.monitor.TimestampRetriever;

@Tag("slow")
class ChecksumUploadNecessityCheckStrategyTest extends AbstractBucketIT {
    private static Connection connection;
    private static ChecksumUploadNecessityCheckStrategy uploadCheck;
    private Bucket bucket;

    @BeforeAll
    static void beforeAll() {
        connection = EXASOL.createConnection();
        uploadCheck = new ChecksumUploadNecessityCheckStrategy(connection);
    }

    @AfterAll
    static void afterAll() throws SQLException {
        connection.close();
    }

    private SyncAwareBucket getBucket() {
        final var bucketConfiguration = getDefaultBucketConfiguration();
        return SyncAwareBucket.builder()//
                .host(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .useTls(dbUsesTls()) //
                .certificate(getDbCertificate()) //
                .allowAlternativeHostName(getHost()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .monitor(createBucketMonitor()) //
                .stateRetriever(new TimestampRetriever()) //
                .build();
    }

    @BeforeEach
    void beforeEach() {
        this.bucket = getBucket();
    }

    @Test
    void testGetSha512Checksum()
            throws NoSuchAlgorithmException, BucketAccessException, InterruptedException, TimeoutException {
        final String testContent = "test";
        final String testFile = "aFileForChecksumTest.txt";
        this.bucket.uploadStringContent(testContent, testFile);
        final String checksum = uploadCheck.getSha512Checksum(testFile, this.bucket);
        final MessageDigest checksumBuilder = MessageDigest.getInstance("SHA-512");
        checksumBuilder.update(testContent.getBytes());
        assertThat(checksum, equalTo(toHex(checksumBuilder.digest())));
    }

    @Test
    void testGetChecksumOfNonExistingFile() {
        final String nunExistingFileName = getUniqueFileName();
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> uploadCheck.getSha512Checksum(nunExistingFileName, this.bucket));
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
        assertFalse(uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    // [itest->dsn~conditional-upload-by-checksum~1]
    void testUploadRequiredByDifferentContent(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException, InterruptedException {
        final String twoMbString = "a".repeat(2000000);
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, twoMbString);
        this.bucket.uploadStringContent("other", fileName);
        assertTrue(uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    // [itest->dsn~conditional-upload-by-existence~1]
    void testUploadRequiredByNewFile(@TempDir final Path tempDir) throws IOException, BucketAccessException {
        final String twoMbString = "a".repeat(2000000);
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, twoMbString);
        assertTrue(uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    //// [itest->dsn~conditional-upload-by-size~1]
    void testUploadRequiredBySmallFile(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final String smallString = "test";
        final String fileName = getUniqueFileName();
        final Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, smallString);
        this.bucket.uploadFile(testFile, fileName);
        assertTrue(uploadCheck.isUploadNecessary(testFile, fileName, this.bucket));
    }

    @Test
    void testIsUploadNecessaryWithNonExistingFile(@TempDir final Path tempDir) {
        final Path nonExistingFile = tempDir.resolve("nonExistingFile.txt");
        final BucketAccessException exception = assertThrows(BucketAccessException.class,
                () -> uploadCheck.isUploadNecessary(nonExistingFile, "nonExistingFile.txt", this.bucket));
        assertThat(exception.getMessage(),
                equalTo("E-BFSJ-17: Failed to check if we need to upload 'nonExistingFile.txt'."));
    }

    private String getUniqueFileName() {
        return "myFile-" + System.currentTimeMillis() + ".txt";
    }
}
