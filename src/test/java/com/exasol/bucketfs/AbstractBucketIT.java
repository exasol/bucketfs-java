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
abstract class AbstractBucketIT {
    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withReuse(true);

    protected Bucket getDefaultBucket() {
        final BucketFsServiceConfiguration serviceConfiguration = EXASOL.getClusterConfiguration() //
                .getBucketFsServiceConfiguration(DEFAULT_BUCKETFS);
        return getBucket(DEFAULT_BUCKET, serviceConfiguration);
    }

    private Bucket getBucket(final String name, final BucketFsServiceConfiguration serviceConfiguration) {
        final BucketConfiguration bucketConfiguration = serviceConfiguration.getBucketConfiguration(name);
        return Bucket.builder()//
                .ipAddress(EXASOL.getContainerIpAddress()) //
                .httpPort(EXASOL.getMappedPort(EXASOL.getDefaultInternalBucketfsPort())) //
                .serviceName(serviceConfiguration.getName()) //
                .name(name) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .monitor(new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(EXASOL))) //
                .build();
    }
}