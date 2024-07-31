package com.exasol.bucketfs.testutil;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.jsonrpc.CommandFactory;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolDockerImageReference;

public class BucketCreator {
    private static final Logger LOGGER = Logger.getLogger(BucketCreator.class.getName());
    private static final String READ_PASSWORD = "READ_PASSWORD";
    private static final String WRITE_PASSWORD = "WRITE_PASSWORD";

    private final String bucketName;
    private final ExasolContainer<? extends ExasolContainer<?>> container;

    public BucketCreator(final Class<?> forClass, final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.bucketName = getUniqueBucketName(forClass);
        this.container = container;
    }

    public BucketCreator createBucket() {
        return createBucket(true, createCommandFactory());
    }

    public BucketCreator createBucket(final boolean isPublic, final CommandFactory factory) {
        commandWithDefaultValues(factory) //
                .isPublic(isPublic) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .execute();
        return this;
    }

    public BucketCreator assumeJsonRpcAvailable() {
        final ExasolDockerImageReference version = this.container.getDockerImageReference();
        assumeTrue(version.getMajor() >= 7,
                "JSON RPC only available with Exasol version 7 or later, " + version + " is not supported.");
        return this;
    }

    public final CreateBucketCommandBuilder commandWithDefaultValues() {
        return commandWithDefaultValues(createCommandFactory());
    }

    private final CreateBucketCommandBuilder commandWithDefaultValues(final CommandFactory factory) {
        return factory.makeCreateBucketCommand() //
                .bucketFsName(DEFAULT_BUCKETFS) //
                .bucketName(this.bucketName);
    }

    public SyncAwareBucket waitUntilBucketExists() throws InterruptedException {
        final SyncAwareBucket bucket = getSyncAwareBucket();
        final Instant start = Instant.now();
        final Duration timeout = Duration.ofSeconds(5);
        LOGGER.fine(
                () -> "Waiting " + timeout + " until bucket " + bucket.getFullyQualifiedBucketName() + " exists...");
        BucketAccessException exception = null;
        do {
            exception = bucketExists(bucket);
            delayNextCheck();
            final Duration waitingTime = Duration.between(start, Instant.now());
            if (timeout.minus(waitingTime).isNegative()) {
                fail("Time out trying to verify that Bucket '" + bucket.getBucketName() + "' exists after waiting "
                        + waitingTime, exception);
            }
        } while (exception != null);
        LOGGER.fine(() -> "Bucket " + bucket.getFullyQualifiedBucketName() + " exists after "
                + Duration.between(start, Instant.now()));
        return bucket;
    }

    private SyncAwareBucket getSyncAwareBucket() {
        return SyncAwareBucket.builder() //
                .host(this.container.getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .useTls(dbUsesTls()) //
                .certificate(container.getTlsCertificate().orElse(null)) //
                .allowAlternativeHostName(this.container.getHost()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(this.bucketName) //
                .readPassword(READ_PASSWORD) //
                .writePassword(WRITE_PASSWORD) //
                .monitor(createBucketMonitor()) //
                .stateRetriever(new TimestampRetriever()) //
                .build();
    }

    @SuppressWarnings("java:S2925") // Sleep required for waiting until bucket is available
    private void delayNextCheck() throws InterruptedException {
        Thread.sleep(300);
    }

    private BucketAccessException bucketExists(final SyncAwareBucket bucket) {
        try {
            bucket.listContents();
        } catch (final BucketAccessException exception) {
            return exception;
        }
        return null;
    }

    private Integer getMappedDefaultBucketFsPort() {
        return this.container.getMappedPort(this.container.getDefaultInternalBucketfsPort());
    }

    private boolean dbUsesTls() {
        return this.container.getDefaultInternalBucketfsPort() == AbstractBucketIT.BUCKETFS_TLS_PORT;
    }

    private LogBasedBucketFsMonitor createBucketMonitor() {
        return new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(this.container), FilterStrategy.TIME_STAMP);
    }

    private String getUniqueBucketName(final Class<?> forClass) {
        return forClass.getSimpleName() + "_" + System.currentTimeMillis();
    }

    private CommandFactory createCommandFactory() {
        return createCommandFactory(false);
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public CommandFactory createCommandFactory(final boolean raiseTlsErrors) {
        final String authenticationToken = this.container.getClusterConfiguration().getAuthenticationToken();
        return CommandFactory.builder() //
                .serverUrl(this.container.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken) //
                .raiseTlsErrors(raiseTlsErrors).build();
    }
}
