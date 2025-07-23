package com.exasol.bucketfs;

/**
 * Defines a strategy for waiting until a bucket in BucketFS is ready for operations. Implementations of this interface
 * should provide the logic to wait for a Bucket to reach a ready state.
 */
public interface BucketReadyWaitStrategy {
    /**
     * Waits until the specified bucket is ready for operations. This method should block until the bucket reaches a
     * ready state.
     *
     * @param bucket bucket to wait for
     */
    void waitUntilBucketIsReady(final Bucket bucket);
}
