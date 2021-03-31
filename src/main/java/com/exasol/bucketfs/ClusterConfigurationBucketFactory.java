package com.exasol.bucketfs;

import java.util.HashMap;
import java.util.Map;

import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class ClusterConfigurationBucketFactory implements BucketFactory {
    private final Map<String, Bucket> bucketsCache = new HashMap<>();
    private final String ipAddress;
    private final BucketFsSerivceConfigurationProvider serviceConfigurationProvider;
    private final Map<Integer, Integer> portMappings;
    private final BucketFsMonitor monitor;

    /**
     * Create a new instance of a {@link ClusterConfigurationBucketFactory}.
     *
     * @param monitor                      BucketFS synchronization monitor
     * @param ipAddress                    IP address of the the BucketFS service
     * @param serviceConfigurationProvider provider for the configuration of BucketFS services
     * @param portMappings                 mapping of container internal to exposed port numbers
     */
    public ClusterConfigurationBucketFactory(final BucketFsMonitor monitor, final String ipAddress,
            final BucketFsSerivceConfigurationProvider serviceConfigurationProvider,
            final Map<Integer, Integer> portMappings) {
        this.ipAddress = ipAddress;
        this.serviceConfigurationProvider = serviceConfigurationProvider;
        this.portMappings = portMappings;
        this.monitor = monitor;
    }

    private int mapPort(final int internalPort) {
        return this.portMappings.get(internalPort);
    }

    @Override
    public synchronized Bucket getBucket(final String serviceName, final String bucketName) {
        final String cacheKey = getFullyQualifiedBucketName(serviceName, bucketName);
        updateBucketCache(serviceName, bucketName, cacheKey);
        return getBucketFromCache(cacheKey);
    }

    private String getFullyQualifiedBucketName(final String serviceName, final String bucketName) {
        return serviceName + BucketConstants.PATH_SEPARATOR + bucketName;
    }

    public Bucket getBucketFromCache(final String cacheKey) {
        return this.bucketsCache.get(cacheKey);
    }

    private void updateBucketCache(final String serviceName, final String bucketName, final String cacheKey) {
        final BucketFsServiceConfiguration serviceConfiguration = this.serviceConfigurationProvider
                .getBucketFsServiceConfiguration(serviceName);
        final BucketConfiguration bucketConfiguration = serviceConfiguration.getBucketConfiguration(bucketName);
        this.bucketsCache.computeIfAbsent(cacheKey, bucket -> SyncAwareBucket //
                .builder() //
                .monitor(this.monitor) //
                .serviceName(serviceName) //
                .name(bucketName) //
                .ipAddress(this.ipAddress) //
                .httpPort(mapPort(serviceConfiguration.getHttpPort())) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .build());
    }
}