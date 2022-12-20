package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;
import com.exasol.containers.ExasolContainer;

/**
 * Abstract base for bucket integration tests.
 */
@Testcontainers
public abstract class AbstractBucketIT {

    /**
     * ExasolContainer
     */
    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withReuse(true);

    /**
     * Get the container Ip Address.
     *
     * @return String
     */
    protected String getHost() {
        return EXASOL.getHost();
    }

    /**
     * Get the Mapped Default Bucket Fs Port.
     *
     * @return Integer
     */
    protected Integer getMappedDefaultBucketFsPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalBucketfsPort());
    }

    /**
     * Get the mapped json rpc port.
     *
     * @return mapped json rpc port
     */
    protected Integer getMappedJsonRpcPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalRpcPort());
    }

    /**
     * Get the default bucket configuration.
     *
     * @return default bucket configuration
     */
    protected BucketConfiguration getDefaultBucketConfiguration() {
        return getDefaultBucketFsServiceConfiguration().getBucketConfiguration(DEFAULT_BUCKET);
    }

    private BucketFsServiceConfiguration getDefaultBucketFsServiceConfiguration() {
        return EXASOL.getClusterConfiguration() //
                .getBucketFsServiceConfiguration(DEFAULT_BUCKETFS);
    }

    /**
     * Get the log based bucket fs monitor.
     *
     * @return LogBasedBucketFsMonitor
     */
    protected LogBasedBucketFsMonitor createBucketMonitor() {
        return new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(EXASOL), FilterStrategy.TIME_STAMP);
    }

    protected SyncAwareBucket getDefaultBucketForWriting() {
        final BucketConfiguration config = getDefaultBucketConfiguration();
        return getDefaultBucketForWriting(config.getReadPassword(), config.getWritePassword());
    }

    protected SyncAwareBucket getDefaultBucketForWriting(final String readPassword, final String writePassword) {
        final LogBasedBucketFsMonitor monitor = createBucketMonitor();
        return SyncAwareBucket.builder()//
                .host(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(readPassword) //
                .writePassword(writePassword) //
                .monitor(monitor) //
                .stateRetriever(new TimestampRetriever()) //
                .build();
    }
}