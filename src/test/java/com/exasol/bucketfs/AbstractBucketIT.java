package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeoutException;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor;
import com.exasol.bucketfs.testcontainers.LogBasedBucketFsMonitor.FilterStrategy;
import com.exasol.clusterlogs.LogPatternDetectorFactory;
import com.exasol.config.BucketConfiguration;
import com.exasol.config.BucketFsServiceConfiguration;
import com.exasol.containers.ExasolContainer;

/**
 * Abstract base for bucket integration tests.
 */
@Testcontainers
public abstract class AbstractBucketIT {

    public static final int BUCKETFS_TLS_PORT = 2581;

    /**
     * ExasolContainer
     */
    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>() //
            .withReuse(true);

    protected static void uploadFileWithContentToPath(final Bucket bucket, final String content,
                                                      final String pathInBucket) {
        try {
            bucket.uploadStringContent(content, pathInBucket);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Interrupted while uploading file to bucket");
        } catch (final BucketAccessException | TimeoutException exception) {
            fail("Unable to upload test file to bucket", exception);
        }
    }

    /**
     * Get the container Ip Address.
     *
     * @return String
     */
    protected String getHost() {
        return EXASOL.getHost();
    }

    /**
     * Get the Mapped Default Bucket Fs Port.
     *
     * @return mapped port number
     */
    protected int getMappedDefaultBucketFsPort() {
        final Integer port = EXASOL.getMappedPort(EXASOL.getDefaultInternalBucketfsPort());
        if (port == null) {
            throw new IllegalStateException("Port " + EXASOL.getDefaultInternalBucketfsPort() + " is not mapped");
        }
        return port.intValue();
    }

    /**
     * Check if the current Exasol container uses TLS for BucketFS.
     * 
     * @return {@code true} if the current Exasol container uses TLS for BucketFS
     */
    protected boolean dbUsesTls() {
        return EXASOL.getDefaultInternalBucketfsPort() == BUCKETFS_TLS_PORT;
    }

    /**
     * Get the current Exasol container's TLS certificate.
     * 
     * @return the current Exasol container's TLS certificate
     */
    protected X509Certificate getDbCertificate() {
        return EXASOL.getTlsCertificate().orElse(null);
    }

    /**
     * Get the mapped json rpc port.
     *
     * @return mapped json rpc port
     */
    protected Integer getMappedJsonRpcPort() {
        return EXASOL.getMappedPort(EXASOL.getDefaultInternalRpcPort());
    }

    /**
     * Get the default bucket configuration.
     *
     * @return default bucket configuration
     */
    protected BucketConfiguration getDefaultBucketConfiguration() {
        return getDefaultBucketFsServiceConfiguration().getBucketConfiguration(DEFAULT_BUCKET);
    }

    private BucketFsServiceConfiguration getDefaultBucketFsServiceConfiguration() {
        return EXASOL.getClusterConfiguration() //
                .getBucketFsServiceConfiguration(DEFAULT_BUCKETFS);
    }

    /**
     * Get the log based bucket fs monitor.
     *
     * @return LogBasedBucketFsMonitor
     */
    protected LogBasedBucketFsMonitor createBucketMonitor() {
        return new LogBasedBucketFsMonitor(new LogPatternDetectorFactory(EXASOL), FilterStrategy.TIME_STAMP);
    }

    protected SyncAwareBucket getDefaultBucketForWriting() {
        final BucketConfiguration config = getDefaultBucketConfiguration();
        return getDefaultBucketForWriting(config.getReadPassword(), config.getWritePassword());
    }

    protected SyncAwareBucket getDefaultBucketForWriting(final String readPassword, final String writePassword) {
        final LogBasedBucketFsMonitor monitor = createBucketMonitor();
        return SyncAwareBucket.builder()//
                .host(getHost()) //
                .port(getMappedDefaultBucketFsPort()) //
                .useTls(dbUsesTls()) //
                .certificate(getDbCertificate()) //
                .allowAlternativeHostName(getHost()) //
                .serviceName(DEFAULT_BUCKETFS) //
                .name(DEFAULT_BUCKET) //
                .readPassword(readPassword) //
                .writePassword(writePassword) //
                .monitor(monitor) //
                .stateRetriever(new TimestampRetriever()) //
                .build();
    }
}
