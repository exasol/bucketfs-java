package com.exasol.bucketfs;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.io.IOException;
import java.time.Instant;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

/**
 * LogBasedBucketFsMonitor
 */
public class LogBasedBucketFsMonitor implements BucketFsMonitor {
    private final LogPatternDetectorFactory detectorFactory;

    /**
     * @param detectorFactory detectorFactory
     */
    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory) {
        this.detectorFactory = detectorFactory;
    }

    @Override
    public boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final Instant afterUTC)
            throws BucketAccessException {
        try {
            return createBucketLogPatternDetector(pathInBucket, afterUTC).isPatternPresent();
        } catch (final IOException exception) {
            throw new BucketAccessException("Unable to check if object '" + pathInBucket
                    + "' is synchronized in bucket '" + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "'.",
                    exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket, final Instant afterUTC) {
        final var pattern = pathInBucket + ".*" + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern, afterUTC);
    }

    private static boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final var extension : UnsynchronizedBucket.SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
