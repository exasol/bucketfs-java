package com.exasol.bucketfs;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;

import java.io.IOException;
import java.time.Instant;

import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;

public class LogBasedBucketFsMonitor implements BucketFsMonitor {
    private final LogPatternDetectorFactory detectorFactory;

    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory) {
        this.detectorFactory = detectorFactory;
    }

    @Override
    public boolean isObjectSynchronized(final Bucket bucket, final String pathInBucket, final Instant afterUTC)
            throws InterruptedException, BucketAccessException {
        try {
            return createBucketLogPatternDetector(pathInBucket).isPatternPresentAfter(afterUTC);
        } catch (

        final IOException exception) {
            throw new BucketAccessException(
                    "Unable to check if object \"" + pathInBucket + "\" is synchronized in bucket \""
                            + bucket.getBucketFsName() + "/" + bucket.getBucketName() + "\".",
                    exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket) {
        final String pattern = pathInBucket + ".*"
                + (Bucket.isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern);
    }
}