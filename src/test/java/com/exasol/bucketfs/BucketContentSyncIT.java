package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exasol.bucketfs.monitor.TimestampRetriever;

@Tag("slow")
class BucketContentSyncIT extends AbstractBucketIT {
    private static final RandomFileGenerator GENERATOR = new RandomFileGenerator();

    private Bucket getDefaultBucket() {
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

    // [itest->dsn~waiting-until-file-appears-in-target-directory~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Test
    void testWaitForFileToAppear(@TempDir final Path tempDir)
            throws BucketAccessException, IOException, TimeoutException {
        final var filename = "large-file.txt";
        final var tempFile = tempDir.resolve(filename);
        GENERATOR.createRandomFile(tempFile, 10000);
        assertObjectSynchronized(tempFile, getDefaultBucket(), filename);
    }

    private void assertObjectSynchronized(final Path tempFile, final Bucket bucket, final String pathInBucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException {
        final var now = new TimestampRetriever().getState();
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(false));
        bucket.uploadFile(tempFile, pathInBucket);
        assertThat(bucket.isObjectSynchronized(pathInBucket, now), equalTo(true));
    }

    // [itest->dsn~waiting-until-archive-extracted~1]
    // [itest->dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1]
    @Test
    void testWaitForArchiveToBeExtracted(@TempDir final Path tempDir)
            throws IOException, BucketAccessException, TimeoutException {
        final String filename = "archive.zip";
        final Path tempFile = tempDir.resolve(filename);
        createArchive(tempFile);
        assertObjectSynchronized(tempFile, getDefaultBucket(), filename);
    }

    private void createArchive(final Path file) throws IOException {
        final var zip = new ZipOutputStream(new FileOutputStream(file.toFile()));
        final var entry = new ZipEntry("random.txt");
        zip.putNextEntry(entry);
        zip.write("Random bytes:\n".getBytes());
        GENERATOR.writeRandomBytesToStream(zip, 10000);
        zip.closeEntry();
        zip.close();
    }
}
