package com.exasol.bucketfs;

/**
 * Common interface for factories producing {@code Bucket} instances.
 */
public interface BucketFactory {
    /**
     * Get a BucketFS bucket.
     *
     * @param serviceName name of the BucketFS service that hosts the bucket
     * @param bucketName  name of the bucket
     * @return bucket
     */
    public WriteEnabledBucket getBucket(final String serviceName, final String bucketName);
}