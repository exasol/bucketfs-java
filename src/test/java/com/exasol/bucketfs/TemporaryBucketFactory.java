package com.exasol.bucketfs;

import com.exasol.bucketfs.jsonrpc.CommandFactory;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.BucketFsServiceConfiguration;
import com.exasol.containers.ExasolContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;

/**
 * Factory for creating temporary buckets in Exasol's BucketFS.
 * <p>
 * This class is intended for creating disposable buckets with unique names in integration tests. All buckets are
 * created in the scope of the default BucketFS service.
 * </p>
 * <p>
 * Note that unlike in the production factory the bucket objects are <em>not</em> cashed! You get the bucket
 * object with a {@code create…} method and need to keep the reference.
 * </p>
 */
public class TemporaryBucketFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemporaryBucketFactory.class);
    private static final String DEFAULT_WRITE_PASSWORD = "WRITE";
    private static final String DEFAULT_READ_PASSWORD = "READ";
    public static final TimestampRetriever TIMESTAMP_RETRIEVER = new TimestampRetriever();
    private final ExasolContainer<? extends ExasolContainer<?>> container;
    private final BucketFsMonitor monitor;
    private final BucketFsServiceConfiguration serviceConfiguration;
    private final BucketReadyWaitStrategy bucketReadyWaitStrategy;

    public TemporaryBucketFactory(final ExasolContainer<? extends ExasolContainer<?>> container) {
        this.container = container;
        this.serviceConfiguration = getBucketFsServiceConfiguration(container);
        this.monitor = getBucketFsMonitor(container);
        this.bucketReadyWaitStrategy = new BucketReadyToListWaitStrategy();
    }

    private static BucketFsServiceConfiguration getBucketFsServiceConfiguration(final ExasolContainer<? extends ExasolContainer<?>> container) {
        return container.getClusterConfiguration().getBucketFsServiceConfiguration(DEFAULT_BUCKETFS);
    }

    private BucketFsMonitor getBucketFsMonitor(final ExasolContainer<? extends ExasolContainer<?>> container) {
        final LogPatternDetectorFactory detectorFactory = new LogPatternDetectorFactory(container);
        final LogBasedBucketFsMonitor.FilterStrategy filterStrategy = LogBasedBucketFsMonitor.FilterStrategy.TIME_STAMP;
        return new LogBasedBucketFsMonitor(detectorFactory, filterStrategy);
    }

    public Bucket createPublicBucket() {
        return createBucket(true, DEFAULT_READ_PASSWORD, DEFAULT_WRITE_PASSWORD);
    }

    private Bucket createBucket(final boolean isPublic, final String readPassword, final String writePassword) {
        final String uniqueBucketName = "bucket_" + UUID.randomUUID();
        LOGGER.info("Creating temporary bucket {}", uniqueBucketName);
        final CommandFactory commandFactory = createCommandFactory();
        commandFactory.makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS)
                .bucketName(uniqueBucketName)
                .readPassword(readPassword)
                .writePassword(writePassword)
                .isPublic(isPublic)
                .execute();
        final Integer mappedPort = this.container.getMappedPort(detectPort());
        final Bucket bucket = (Bucket) SyncAwareBucket.builder()
                .monitor(this.monitor)
                .stateRetriever(TIMESTAMP_RETRIEVER)
                .host("localhost")
                .port(mappedPort)
                .readPassword(readPassword)
                .writePassword(writePassword)
                .serviceName(DEFAULT_BUCKETFS)
                .name(uniqueBucketName)
                .build();
        this.bucketReadyWaitStrategy.waitUntilBucketIsReady(bucket);
        return bucket;
    }

    /**
     * Prefer using the HTTP port if available. Fall back to HTTPS otherwise.
     *
     * @return a configured port on which the BucketFS service is reachable.
     */
    private int detectPort() {
        return (this.serviceConfiguration.getHttpPort() == 0)
                ? this.serviceConfiguration.getHttpsPort()
                : this.serviceConfiguration.getHttpPort();
    }

    public Bucket createPrivateBucket() {
        return createBucket(false, DEFAULT_READ_PASSWORD, DEFAULT_WRITE_PASSWORD);
    }

    private CommandFactory createCommandFactory() {
        return createCommandFactory(false);
    }

    public CommandFactory createCommandFactory(final boolean raiseTlsErrors) {
        final String authenticationToken = this.container.getClusterConfiguration().getAuthenticationToken();
        return CommandFactory.builder() //
                .serverUrl(this.container.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken) //
                .raiseTlsErrors(raiseTlsErrors).build();
    }
}
