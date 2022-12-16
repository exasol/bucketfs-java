package com.exasol.bucketfs;

import java.net.http.HttpClient;
import java.util.*;

import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;

/**
 * Factory for objects abstracting buckets in Exasol's BucketFS.
 */
public final class ClusterConfigurationBucketFactory implements BucketFactory {
    private final Map<String, Bucket> bucketsCache = new HashMap<>();
    private final String host;
    private final BucketFsServiceConfigurationProvider serviceConfigurationProvider;
    private final Map<Integer, Integer> portMappings;
    private final BucketFsMonitor monitor;

    /**
     * Create a new instance of a {@link ClusterConfigurationBucketFactory}.
     *
     * @param monitor                      BucketFS synchronization monitor
     * @param host                    IP address of the the BucketFS service
     * @param serviceConfigurationProvider provider for the configuration of BucketFS services
     * @param portMappings                 mapping of container internal to exposed port numbers
     */
    public ClusterConfigurationBucketFactory(final BucketFsMonitor monitor, final String host,
            final BucketFsServiceConfigurationProvider serviceConfigurationProvider,
            final Map<Integer, Integer> portMappings) {
        this.host = host;
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

    public List<String> listBuckets(final String serviceName) throws BucketAccessException {
        final BucketFsServiceConfiguration serviceConfiguration = this.serviceConfigurationProvider
                .getBucketFsServiceConfiguration(serviceName);
        final HttpClient client = new HttpClientBuilder() //
                // .certificate(...)
                // .raiseTlsErrors(...)
                .build();
        return ListingProvider.builder() //
                // .serviceName(serviceName) //
                .httpClient(client) //
                .protocol("http") // could as well be "https"
                .host(this.host) //
                .port(serviceConfiguration.getHttpPort()) //
                .build() //
                .listContents();
    }

    private String getFullyQualifiedBucketName(final String serviceName, final String bucketName) {
        return serviceName + BucketConstants.PATH_SEPARATOR + bucketName;
    }

    /**
     * Returns bucket from cache.
     *
     * @param cacheKey cache key
     * @return bucket
     */
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
                .stateRetriever(new TimestampRetriever()) //
                .serviceName(serviceName) //
                .name(bucketName) //
                .host(this.host) //
                .port(mapPort(serviceConfiguration.getHttpPort())) //
                .readPassword(bucketConfiguration.getReadPassword()) //
                .writePassword(bucketConfiguration.getWritePassword()) //
                .build());
    }
}