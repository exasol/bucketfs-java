package com.exasol.bucketfs;

/**
 * This class takes bucket objects as prototypes and copies them.
 * <p>
 * This is useful in case tests need to modify settings before accessing the buckets. A typical example would be a
 * bad-weather case where you intentionally inject the wrong password to check whether the resulting error is handled
 * correctly.
 * </p>
 */
public final class BucketObjectReplicator {
    private BucketObjectReplicator() {
        // Prevent instantiation
    }

    /**
     * Create a bucket builder with a copy of a given bucket's configuration.
     *
     * @param bucket prototype bucket
     *
     * @return builder initialized with a copy of the prototype's configuration
     */
    static ReadEnabledBucket.Builder<? extends ReadEnabledBucket.Builder<?>> copyBucket(final Bucket bucket) {
        return ReadEnabledBucket.builder()
                .host(bucket.getHost())
                .port(bucket.getPort())
                .serviceName(bucket.getBucketFsName())
                .name(bucket.getBucketName())
                .readPassword(bucket.getReadPassword())
                .useTls(bucket.getProtocol() == "https")
                .raiseTlsErrors(false);
    }
}
