package com.exasol.bucketfs;

import com.exasol.config.BucketFsServiceConfiguration;

/**
 * Interface for objects that can provide the configuration of a BucketFS service.
 */
public interface BucketFsSerivceConfigurationProvider {
    /**
     * Get the BucketFS service configuration for a given service name.
     *
     * @param serviceName name of the service for which the configuration should be provided
     * @return service configuration
     */
    public BucketFsServiceConfiguration getBucketFsServiceConfiguration(String serviceName);
}