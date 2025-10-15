package com.exasol.bucketfs;

/**
 * This class takes bucket objects as prototypes and copies them.
 * <p>
 * This is useful when tests need to modify settings before accessing the buckets. A typical example would be a
 * bad-weather scenario, where you intentionally inject the wrong password to verify that the resulting error is handled
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
                .useTls("https".equals(bucket.getProtocol()))
                .raiseTlsErrors(false);
    }
}
