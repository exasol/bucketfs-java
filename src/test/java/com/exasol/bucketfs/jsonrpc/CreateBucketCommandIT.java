package com.exasol.bucketfs.jsonrpc;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.monitor.TimestampRetriever;
import com.exasol.bucketfs.uploadnecessity.JsonRpcReadyWaitStrategy;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.*;

@Tag("slow")
// [itest->dsn~creating-new-bucket~1]
class CreateBucketCommandIT extends AbstractBucketIT {
    private static X509Certificate tlsCertificate;

    @BeforeAll
    static void beforeAll() {
        final Optional<X509Certificate> optionalX509Certificate = EXASOL.getTlsCertificate();
        if (optionalX509Certificate.isPresent()) {
            tlsCertificate = optionalX509Certificate.get();
        } else  {
            fail("This test requires a TLS certificate to be set up in the Exasol database.");
        }
    }

    @Test
    void testCreatingBucketWithCheckingCertificateThrowsException() {
        final CommandFactory commandFactory = createCommandFactory()
                .raiseTlsErrors(true)
                .build();
        final CreateBucketCommand.CreateBucketCommandBuilder commandBuilder = commandFactory.makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS)
                .bucketName("CertCheckFailingBucket_" + UUID.randomUUID())
                .isPublic(true);
        final JsonRpcException exception = assertThrows(JsonRpcException.class, commandBuilder::execute);
        assertAll(
                () -> assertThat(exception.getMessage(),
                        containsString("E-BFSJ-23: Unable to execute RPC request https://")), //
                () -> assertThat(exception.getCause().getMessage(),
                        equalTo("PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:"
                                + " unable to find valid certification path to requested target")));
    }

    private static CommandFactory.Builder createCommandFactory() {
        return CommandFactory.builder()
                .serverUrl(EXASOL.getRpcUrl())
                .bearerTokenAuthentication(EXASOL.getClusterConfiguration().getAuthenticationToken());
    }

    @Test
    void testCreatingBucketWithCertificateFailsHostnameValidation() {
        Assumptions.assumeTrue(EXASOL.getDockerImageReference().getMajor() >= 8);
        final CommandFactory commandFactory = createCommandFactory()
                .raiseTlsErrors(true)
                .certificate(tlsCertificate)
                .build();
        final CreateBucketCommand.CreateBucketCommandBuilder commandBuilder = commandFactory.makeCreateBucketCommand()
                .bucketFsName(DEFAULT_BUCKETFS)
                .bucketName("HostNameValidationFailingBucket_" + UUID.randomUUID())
                .isPublic(true);
        final JsonRpcException exception = assertThrows(JsonRpcException.class, commandBuilder::execute);
        assertAll(
                () -> assertThat(exception.getMessage(),
                        containsString("E-BFSJ-23: Unable to execute RPC request https://")), //
                () -> assertThat(exception.getCause().getMessage(),
                        either(equalTo("No subject alternative names present"))
                                .or(equalTo("No name matching localhost found"))
                                .or(equalTo("No subject alternative DNS name matching localhost found."))));
    }

    @Test
    void createBucketWithExistingNameFails() {
        final TemporaryBucketFactory bucketFactory = new TemporaryBucketFactory(EXASOL);
        final Bucket bucket = bucketFactory.createPublicBucket();
        final CommandFactory commandFactory = createCommandFactory()
                .raiseTlsErrors(false)
                .build();
        final CreateBucketCommand.CreateBucketCommandBuilder commandBuilder = commandFactory.makeCreateBucketCommand()
                .bucketFsName(bucket.getBucketFsName())
                .bucketName(bucket.getBucketName())
                .isPublic(true);
        final JsonRpcException exception = assertThrows(JsonRpcException.class, commandBuilder::execute);
        assertThat(exception.getMessage(), containsString(
                "Given bucket " + bucket.getBucketName() + " already exists in bucketfs " + DEFAULT_BUCKETFS));
    }

    @Test
    void createBucketWithoutCertificateCheckSucceeds() {
        final String bucketName = "WriteTestBucket_" + UUID.randomUUID();
        final String writeTestPassword = "Write me!";
        final String readTestPassword = "Read me!";
        final ExasolVersionCapabilities capabilities = ExasolVersionCapabilities.of(EXASOL);
        final CommandFactory commandFactory = createCommandFactory()
                .raiseTlsErrors(false)
                .build();
        new JsonRpcReadyWaitStrategy().waitUntilXmlRpcReady();
        commandFactory.makeCreateBucketCommand()
                .useBase64EncodedPasswords(capabilities.requiresBase64EncodingBucketFsPasswordsOnClientSide())
                .bucketFsName(DEFAULT_BUCKETFS)
                .bucketName(bucketName)
                .isPublic(true)
                .writePassword(writeTestPassword)
                .readPassword(readTestPassword)
                .execute();
        final SyncAwareBucket bucket = (SyncAwareBucket) SyncAwareBucket.builder()
                .monitor(createBucketMonitor())
                .stateRetriever(new TimestampRetriever())
                .serviceName(DEFAULT_BUCKETFS)
                .name(bucketName)
                .host(getHost())
                .port(getMappedDefaultBucketFsPort())
                .writePassword(writeTestPassword)
                .readPassword(readTestPassword)
                .useTls(true)
                .raiseTlsErrors(false)
                .build();
        final BucketReadyWaitStrategy waitStrategy = new BucketReadyToListWaitStrategy();
        waitStrategy.waitUntilBucketIsReady(bucket);
        assertBucketWritable(bucket);
    }

    private void assertBucketWritable(final SyncAwareBucket bucket) {
        final String fileName = "test-uploaded.txt";
        try {
            bucket.uploadStringContent("file content", fileName);
            assertThat(bucket.listContents(), hasItem(fileName));
        } catch (final BucketAccessException exception) {
            fail("Unable to upload test file to bucket '" + bucket.getBucketName() + "'", exception);
        } catch (final TimeoutException exception) {
            fail("Timeout while uploading test file to bucket '" + bucket.getBucketName() + "'", exception);
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Interrupted while uploading test file to bucket '" + bucket.getBucketName() + "'", exception);
        }
    }
}
