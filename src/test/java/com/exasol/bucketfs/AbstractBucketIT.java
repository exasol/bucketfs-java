package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;
import com.exasol.containers.ExasolContainer;

/**
 * Abstract class used for integration tests
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
     * @return String
     */
    protected String getContainerIpAddress() {
        return EXASOL.getContainerIpAddress();
    }

    /**
     * @return Integer
     */
    protected Integer getMappedDefaultBucketFsPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalBucketfsPort());
    }

    /**
     * @return mapped json rpc port
     */
    protected Integer getMappedJsonRpcPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalRpcPort());
    }

    /**
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
     * @return LogBasedBucketFsMonitor
     */
    protected LogBasedBucketFsMonitor createBucketMonitor() {
        return new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(EXASOL));
    }
}