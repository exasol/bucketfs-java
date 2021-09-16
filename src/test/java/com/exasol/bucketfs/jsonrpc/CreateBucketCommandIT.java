package com.exasol.bucketfs.jsonrpc;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.jsonrpc.CommandFactory.Builder;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.containers.ExasolDockerImageReference;

@Tag("slow")
// [itest->dsn~creating-new-bucket~1]
class CreateBucketCommandIT extends AbstractBucketIT {

    private static final String READ_PASSWORD = "READ_PASSWORD";
    private static final String WRITE_PASSWORD = "WRITE_PASSWORD";

    @Test
    void creatingBucketWithCheckingCertificateFails() throws BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        final CreateBucketCommandBuilder command = createCommandFactory(false).makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS).bucketName(randomBucketName).isPublic(true).readPassword(READ_PASSWORD)
                .writePassword(WRITE_PASSWORD);

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());
        assertThat(exception.getMessage(), containsString("Error executing request"));
        assertThat(exception.getCause().getMessage(), equalTo(
                "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target"));
    }

    @Test
    void creatingBucketWithExistingNameFails() throws BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        final CreateBucketCommandBuilder command = createCommandFactory().makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .isPublic(true) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD);

        command.execute();

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());
        assertThat(exception.getMessage(),
                containsString("Given bucket " + randomBucketName + " already exists in bucketfs " + DEFAULT_BUCKETFS));
    }

    @Test
    void createdBucketIsWriteable() throws BucketAccessException, TimeoutException, InterruptedException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        createCommandFactory().makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .isPublic(true) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .execute();
        assertBucketWritable(randomBucketName);
    }

    @Test
    void createdBucketWithDefaultValuesSucceeds() throws BucketAccessException, TimeoutException, InterruptedException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getRandomBucketName();

        assertDoesNotThrow(() -> createCommandFactory().makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .execute());
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
        return createCommandFactory(true);
    }

    private CommandFactory createCommandFactory(final boolean ignoreSslErrors) {
        final String authenticationToken = EXASOL.getClusterConfiguration().getAuthenticationToken();
        final Builder builder = CommandFactory.builder() //
                .serverUrl(EXASOL.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken);
        if (ignoreSslErrors) {
            builder.ignoreSslErrors();
        }
        return builder.build();
    }

    private void assertBucketWritable(final String randomBucketName)
            throws BucketAccessException, TimeoutException, InterruptedException {
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
            delayNextCheck();
            final Duration waitingTime = Duration.between(start, Instant.now());
            if (timeout.minus(waitingTime).isNegative()) {
                fail("Bucket " + bucket.getBucketName() + " not available after " + waitingTime);
            }
        }
    }

    @SuppressWarnings("java:S2925") // Sleep required for waiting until bucket is available
    private void delayNextCheck() throws InterruptedException {
        Thread.sleep(100);
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
