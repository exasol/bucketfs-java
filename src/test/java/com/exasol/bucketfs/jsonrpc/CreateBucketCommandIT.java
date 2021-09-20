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
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.containers.ExasolDockerImageReference;

@Tag("slow")
@Testcontainers
// [itest->dsn~creating-new-bucket~1]
class CreateBucketCommandIT extends AbstractBucketIT {
    private static final String READ_PASSWORD = "READ_PASSWORD";
    private static final String WRITE_PASSWORD = "WRITE_PASSWORD";

    @Test
    void testCreatingBucketWithCheckingCertificateThrowsException() throws BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getUniqueBucketName();

        final CreateBucketCommandBuilder command = createCommandFactory(true).makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .isPublic(true) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD);

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());

        assertAll(
                () -> assertThat(exception.getMessage(),
                        containsString("E-BFSJ-23: Unable to execute RPC request https://")), //
                () -> assertThat(exception.getCause().getMessage(), equalTo(
                        "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target")));
    }

    private void assumeJsonRpcAvailable() {
        final ExasolDockerImageReference version = EXASOL.getDockerImageReference();
        assumeTrue(version.getMajor() >= 7,
                "JSON RPC only available with Exasol version 7 or later, " + version + " is not supported.");
    }

    private String getUniqueBucketName() {
        return this.getClass().getSimpleName() + "_" + System.currentTimeMillis();
    }

    private CommandFactory createCommandFactory(final boolean raiseTlsErrors) {
        final String authenticationToken = EXASOL.getClusterConfiguration().getAuthenticationToken();
        return CommandFactory.builder() //
                .serverUrl(EXASOL.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken) //
                .raiseTlsErrors(raiseTlsErrors).build();
    }

    @Test
    void testCreatingBucketWithExistingNameFails() throws BucketAccessException, TimeoutException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getUniqueBucketName();

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

    private CommandFactory createCommandFactory() {
        return createCommandFactory(false);
    }

    @Test
    void testCreatedBucketIsWriteable() throws BucketAccessException, TimeoutException, InterruptedException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getUniqueBucketName();

        createCommandFactory().makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .isPublic(true) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .execute();
        assertBucketWritable(randomBucketName);
    }

    private void assertBucketWritable(final String randomBucketName)
            throws BucketAccessException, TimeoutException, InterruptedException {
        final SyncAwareBucket bucket = createBucket(randomBucketName);

        waitUntilBucketExists(bucket);

        final var fileName = "test-uploaded.txt";
        bucket.uploadStringContent("file content", fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
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

    private void waitUntilBucketExists(final SyncAwareBucket bucket) throws InterruptedException {
        final Instant start = Instant.now();
        final Duration timeout = Duration.ofSeconds(5);

        while (!bucketExists(bucket)) {
            delayNextCheck();
            final Duration waitingTime = Duration.between(start, Instant.now());
            if (timeout.minus(waitingTime).isNegative()) {
                fail("Time out trying to verify that Bucket '" + bucket.getBucketName() + "' exists after waiting "
                        + waitingTime);
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
        } catch (final BucketAccessException exception) {
            return false;
        }
        return true;
    }

    @Test
    void testCreatedBucketWithDefaultValues() throws BucketAccessException, TimeoutException, InterruptedException {
        assumeJsonRpcAvailable();

        final String randomBucketName = getUniqueBucketName();

        assertDoesNotThrow(() -> createCommandFactory().makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(randomBucketName) //
                .execute());
    }
}
