package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag("slow")
class BucketContentSyncIT extends AbstractBucketIT {
    private static RandomFileGenerator GENERATOR = new RandomFileGenerator();

    private Bucket getDefaultBucket() {
        final var bucketConfiguration = getDefaultBucketConfiguration();
        return SyncAwareBucket.builder()//
                .ipAddress(getContainerIpAddress()) //
                .port(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .monitor(createBucketMonitor()) //
                .build();
    }

    // [itest->dsn~waiting-until-file-appears-in-target-directory~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Test
    void testWaitForFileToAppear(@TempDir final Path tempDir)
            throws BucketAccessException, InterruptedException, IOException, TimeoutException {
        final var filename = "large-file.txt";
        final var tempFile = tempDir.resolve(filename);
        GENERATOR.createRandomFile(tempFile, 10000);
        assertObjectSynchronized(tempFile, getDefaultBucket(), filename);
    }

    private void assertObjectSynchronized(final Path tempFile, final Bucket bucket, final String pathInBucket)
            throws BucketAccessException, InterruptedException, TimeoutException, FileNotFoundException {
        final var now = Instant.now();
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(false));
        bucket.uploadFile(tempFile, pathInBucket);
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(true));
    }

    // [itest->dsn~waiting-until-archive-extracted~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Test
    void testWaitForArchiveToBeExtracted(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, InterruptedException, TimeoutException {
        final String filename = "archive.zip";
        final Path tempFile = tempDir.resolve(filename);
        createArchive(tempFile);
        assertObjectSynchronized(tempFile, getDefaultBucket(), filename);
    }

    private void createArchive(final Path file) throws FileNotFoundException, IOException {
        final var zip = new ZipOutputStream(new FileOutputStream(file.toFile()));
        final var entry = new ZipEntry("random.txt");
        zip.putNextEntry(entry);
        zip.write("Random bytes:\n".getBytes());
        GENERATOR.writeRandomBytesToStream(zip, 10000);
        zip.closeEntry();
        zip.close();
    }
}