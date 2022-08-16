package com.exasol.bucketfs.testutil;

import static com.exasol.containers.ExasolContainerConstants.BUCKETFS_DAEMON_LOG_FILENAME_PATTERN;
import static com.exasol.containers.ExasolContainerConstants.EXASOL_CORE_DAEMON_LOGS_PATH;
import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.time.Instant;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.monitor.BucketFsMonitor;
import com.exasol.bucketfs.monitor.TimestampState;
import com.exasol.clusterlogs.LogPatternDetector;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.containers.ExasolDockerImageReference;

/**
 * This {@link BucketFsMonitor} detects if a file was successfully uploaded from the Exasol log files.
 */
public class LogBasedBucketFsMonitor implements BucketFsMonitor {
    private final LogPatternDetectorFactory detectorFactory;
    private final ExasolDockerImageReference dockerImageReference;

    /**
     * Log based bucket fs monitor c'tor.
     *
     * @param detectorFactory detectorFactory
     */
    public LogBasedBucketFsMonitor(final LogPatternDetectorFactory detectorFactory,
            final ExasolDockerImageReference dockerImageReference) {
        this.detectorFactory = detectorFactory;
        this.dockerImageReference = dockerImageReference;
    }

    @Override
    public boolean isObjectSynchronized(final ReadOnlyBucket bucket, final String pathInBucket, final State state)
            throws BucketAccessException {
        try {
            return createBucketLogPatternDetector(pathInBucket, ((TimestampState) state).getTime()).isPatternPresent();
        } catch (final IOException exception) {
            throw new BucketAccessException(messageBuilder("E-BFSJ-28").message( //
                    "Unable to check if object {{path}} is synchronized in bucket {{bucket filesystem}}/{{bucket name}}.", //
                    pathInBucket, bucket.getBucketFsName(), bucket.getBucketName()) //
                    .toString(), //
                    exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(messageBuilder("E-BFSJ-29").message( //
                    "Caught interrupt trying to check if object {{path}} is synchronized in bucket {{bucket filesystem}}/{{bucket name}}.", //
                    pathInBucket, bucket.getBucketFsName(), bucket.getBucketName()) //
                    .toString(), //
                    exception);
        }
    }

    private LogPatternDetector createBucketLogPatternDetector(final String pathInBucket, final Instant afterUTC) {
        return this.detectorFactory.createLogPatternDetector(EXASOL_CORE_DAEMON_LOGS_PATH,
                BUCKETFS_DAEMON_LOG_FILENAME_PATTERN, pattern(pathInBucket), afterUTC);
    }

    private String pattern(final String pathInBucket) {
        if (isOldVersion()) {
            return pathInBucket + ".*" + (isSupportedArchiveFormat(pathInBucket) ? "extracted" : "linked");
        } else {
            return "rsync for .*'" //
                    + (pathInBucket.startsWith("/") ? pathInBucket.substring(1) : pathInBucket) //
                    + ".*'.* is done";
        }
    }

    private boolean isOldVersion() {
        return (this.dockerImageReference.hasMajor() && (this.dockerImageReference.getMajor() < 8));
    }

    private static boolean isSupportedArchiveFormat(final String pathInBucket) {
        for (final String extension : UnsynchronizedBucket.SUPPORTED_ARCHIVE_EXTENSIONS) {
            if (pathInBucket.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
