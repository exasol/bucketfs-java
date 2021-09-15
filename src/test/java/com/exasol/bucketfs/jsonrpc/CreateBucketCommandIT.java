package com.exasol.bucketfs.jsonrpc;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.containers.ExasolDockerImageReference;

@Tag("slow")
class CreateBucketCommandIT extends AbstractBucketIT {

    private static final String READ_PASSWORD = "READ_PASSWORD";
    private static final String WRITE_PASSWORD = "WRITE_PASSWORD";

    @Test
    void creatingBucketWithExistingNameFails() throws InterruptedException, BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        final CreateBucketCommandBuilder command = createCommandFactory().makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS).bucketName(randomBucketName).isPublic(true).readPassword(READ_PASSWORD)
                .writePassword(WRITE_PASSWORD);

        command.execute();

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());
        assertThat(exception.getMessage(),
                containsString("Given bucket " + randomBucketName + " already exists in bucketfs " + DEFAULT_BUCKETFS));
    }

    @Test
    void createdBucketIsWriteable() throws InterruptedException, BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        createCommandFactory().makeCreateBucketCommand().bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .isPublic(true) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .execute();
        assertBucketWritable(randomBucketName);
    }

    private void assumeJsonRpcAvailable() {
        final ExasolDockerImageReference version = EXASOL.getDockerImageReference();
        assumeTrue(version.getMajor() >= 7,
                "JSON RPC only available with version >= 7, " + version + " is not supported.");
    }

    private String getRandomBucketName() {
        return this.getClass().getSimpleName() + "_" + System.currentTimeMillis();
    }

    private CommandFactory createCommandFactory() {
        final String authenticationToken = EXASOL.getClusterConfiguration().getAuthenticationToken();
        return CommandFactory.builder() //
                .serverUrl(EXASOL.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken) //
                .ignoreSslErrors().build();
    }

    private void assertBucketWritable(final String randomBucketName)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final SyncAwareBucket bucket = createBucket(randomBucketName);

        waitUntilBucketExists(bucket);

        final var fileName = "test-uploaded.txt";
        bucket.uploadStringContent("file content", fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }

    private void waitUntilBucketExists(final SyncAwareBucket bucket) throws InterruptedException {
        final Instant start = Instant.now();
        final Duration timeout = Duration.ofSeconds(5);

        while (!bucketExists(bucket)) {
            Thread.sleep(100);
            final Duration waitingTime = Duration.between(start, Instant.now());
            if (timeout.minus(waitingTime).isNegative()) {
                fail("Bucket " + bucket.getBucketName() + " not available after " + waitingTime);
            }
        }
    }

    private boolean bucketExists(final SyncAwareBucket bucket) {
        try {
            bucket.listContents();
        } catch (final BucketAccessException e) {
            return false;
        }
        return true;
    }

    private SyncAwareBucket createBucket(final String bucketName) {
        return SyncAwareBucket.builder() //
                .ipAddress(getContainerIpAddress()) //
                .httpPort(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(bucketName) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .monitor(createBucketMonitor()) //
                .build();
    }
}
