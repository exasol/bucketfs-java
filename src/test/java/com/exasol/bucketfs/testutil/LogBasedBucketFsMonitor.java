package com.exasol.bucketfs.testutil;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.io.IOException;
import java.time.Instant;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampState;
import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * This class is basically a copy of the same class in project "exasol-testcontainers".
 *
 * <p>
 * As soon as project "exasol-testcontainers" has been migrated to bucketfs-java 2.3.0 and has released a newer version
 * v2, this class can be replaced by upgrading dependency exasol-testcontainers to a version v2 and using the class
 * LogBasedBucketFsMonitor from exasol-testcontainers.
 * </p>
 */
public class LogBasedBucketFsMonitor implements BucketFsMonitor {
    private final LogPatternDetectorFactory detectorFactory;

    /**
     * Log based bucket fs monitor c'tor.
     *
     * @param detectorFactory detectorFactory
     */
    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory) {
        this.detectorFactory = detectorFactory;
    }

    @Override
    public boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final State state)
            throws BucketAccessException {
        try {
            return createBucketLogPatternDetector(pathInBucket, ((TimestampState) state).getTime()).isPatternPresent();
        } catch (final IOException exception) {
            throw new BucketAccessException(
                    "Unable to check if object \"" + pathInBucket + "\" is synchronized in bucket \""
                            + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "\".",
                    exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Caught interrupt trying to check if object \"" + pathInBucket + "\" is synchronized in bucket \""
                            + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "\".",
                    exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket, final Instant afterUTC) {
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern(pathInBucket), afterUTC);
    }

    private String pattern(final String pathInBucket) {
        return "removed sync future for id .*'" //
                + (pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket) //
                + ".*'";
    }
}
