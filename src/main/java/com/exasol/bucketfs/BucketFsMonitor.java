package com.exasol.bucketfs;

import java.time.Instant;

/**
 * Monitor that watches the synchronization of objects in BucketFS.
 */
public interface BucketFsMonitor {
    /**
     * Check if the object with the given path is already synchronized.
     *
     * @param bucket       bucket
     * @param pathInBucket path to the object located in the bucket
     * @param afterUTC     point in UTC time after which the object synchronization counts
     * @return {@code true} if the object exists in the bucket and is synchronized
     * @throws InterruptedException  if an interrupt occurs while the object synchronization is being evaluated
     * @throws BucketAccessException if the object in the bucket is inaccessible
     */
    public boolean isObjectSynchronized(final Bucket bucket, final String pathInBucket, final Instant afterUTC)
            throws InterruptedException, BucketAccessException;
}