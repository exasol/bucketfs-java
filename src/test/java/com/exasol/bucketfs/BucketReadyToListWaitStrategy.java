package com.exasol.bucketfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(BucketReadyToListWaitStrategy.class);
    private static final int DELAY_BETWEEN_RETRIES_MS = 300;

    /**
     * Creates a new instance of the {@code BucketReadyToListWaitStrategy}.
     */
    public BucketReadyToListWaitStrategy() {
        // Added to make required JavaDoc available.
    }

    @Override
    public void waitUntilBucketIsReady(final Bucket bucket) {
        LOGGER.info("Waiting until bucket {} is ready...", bucket.getFullyQualifiedBucketName());
        final Duration timeout = Duration.ofSeconds(5);
        final Instant end = Instant.now().plus(timeout);
        do {
            try {
                bucket.listContents();
                return;
            } catch (final BucketAccessException exception) {
                delayPollingRetry();
            }
        } while (Instant.now().isBefore(end));
        fail("Timout after " + timeout + " waiting for bucket to be ready.");
    }


    @SuppressWarnings("java:S2925") // Sleep used to rate limit polling retries
    private static void delayPollingRetry() {
        try {
            
            Thread.sleep(DELAY_BETWEEN_RETRIES_MS);
        } catch (final InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
