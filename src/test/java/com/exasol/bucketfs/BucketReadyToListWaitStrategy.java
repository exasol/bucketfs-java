package com.exasol.bucketfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Implementation of the {@link BucketReadyWaitStrategy} that waits until a bucket is ready by attempting
 * to list its contents. This strategy periodically retries listing the bucket's contents until the operation
 * succeeds or a timeout is reached.
 * <p>
 * If the timeout is reached before the bucket becomes ready, the operation fails.
 * </p>
 */
public class BucketReadyToListWaitStrategy implements BucketReadyWaitStrategy {
    @SuppressWarnings("java:S2925") // Suppressing Sonar warning about Thread.sleep
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketReadyToListWaitStrategy.class);
    public static final Duration WAIT_TIMEOUT = Duration.ofSeconds(60);
    private static final int DELAY_BETWEEN_RETRIES_NANOS = 300000;

    /**
     * Creates a new instance of the {@code BucketReadyToListWaitStrategy}.
     */
    public BucketReadyToListWaitStrategy() {
        // Added to make required JavaDoc available.
    }

    @Override
    public void waitUntilBucketIsReady(final Bucket bucket) {
        LOGGER.info("Waiting until bucket {} is ready for max {}." , bucket.getFullyQualifiedBucketName(), WAIT_TIMEOUT);
        final Instant end = Instant.now().plus(WAIT_TIMEOUT);
        do {
            try {
                bucket.listContents();
                LOGGER.info("Succeeded to list bucket contents: {}. Assuming bucket ready.", bucket);
                return;
            } catch (final BucketAccessException exception) {
                delayPollingRetry();
            }
        } while (Instant.now().isBefore(end));
        fail("Timeout after " + WAIT_TIMEOUT + " waiting for bucket " + bucket + " to be ready.");
    }

    private static void delayPollingRetry() {
        LockSupport.parkNanos(DELAY_BETWEEN_RETRIES_NANOS);
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
        }
    }
}