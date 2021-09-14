package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;
import com.exasol.containers.ExasolContainer;

@Testcontainers
public abstract class AbstractBucketIT {

    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withReuse(true);

    protected String getContainerIpAddress() {
        return EXASOL.getContainerIpAddress();
    }

    protected Integer getMappedDefaultBucketFsPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalBucketfsPort());
    }

    protected Integer getMappedJsonRpcPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalRpcPort());
    }

    protected BucketConfiguration getDefaultBucketConfiguration() {
        return getDefaultBucketFsServiceConfiguration().getBucketConfiguration(DEFAULT_BUCKET);
    }

    private BucketFsServiceConfiguration getDefaultBucketFsServiceConfiguration() {
        return EXASOL.getClusterConfiguration() //
                .getBucketFsServiceConfiguration(DEFAULT_BUCKETFS);
    }

    protected LogBasedBucketFsMonitor createBucketMonitor() {
        return new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(EXASOL));
    }
}