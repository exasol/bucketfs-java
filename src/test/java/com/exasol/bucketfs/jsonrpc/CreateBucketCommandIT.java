package com.exasol.bucketfs.jsonrpc;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.exasol.bucketfs.*;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.bucketfs.testutil.BucketCreator;

@Tag("slow")
// [itest->dsn~creating-new-bucket~1]
class CreateBucketCommandIT extends AbstractBucketIT {

    @Test
    void testCreatingBucketWithCheckingCertificateThrowsException() throws BucketAccessException, TimeoutException {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        final CommandFactory commandFactory = bucketCreator.createCommandFactory(true);
        final CreateBucketCommandBuilder command = bucketCreator.command(commandFactory);
        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());

        assertAll(
                () -> assertThat(exception.getMessage(),
                        containsString("E-BFSJ-23: Unable to execute RPC request https://")), //
                () -> assertThat(exception.getCause().getMessage(),
                        equalTo("PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:"
                                + " unable to find valid certification path to requested target")));
    }

    @Test
    void testCreatingBucketWithCertificateFailsHostnameValidation() throws BucketAccessException, TimeoutException {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        final String authenticationToken = EXASOL.getClusterConfiguration().getAuthenticationToken();
        final CommandFactory commandFactory = CommandFactory.builder() //
                .serverUrl(EXASOL.getRpcUrl()) //
                .bearerTokenAuthentication(authenticationToken) //
                .raiseTlsErrors(true) //
                .certificate(EXASOL.getTlsCertificate().get()) //
                .build();

        final CreateBucketCommandBuilder command = bucketCreator.command(commandFactory);
        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());

        assertAll(
                () -> assertThat(exception.getMessage(),
                        containsString("E-BFSJ-23: Unable to execute RPC request https://")), //
                () -> assertThat(exception.getCause().getMessage(),
                        either(equalTo("No subject alternative names present"))
                                .or(equalTo("No name matching localhost found"))));
    }

    @Test
    void createBucketWithExistingNameFails() throws BucketAccessException, TimeoutException {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        final CreateBucketCommandBuilder command = bucketCreator.command();
        command.execute();

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> command.execute());
        assertThat(exception.getMessage(), containsString(
                "Given bucket " + bucketCreator.getBucketName() + " already exists in bucketfs " + DEFAULT_BUCKETFS));
    }

    @Test
    void createBucketSuccess() throws BucketAccessException, TimeoutException, InterruptedException {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        bucketCreator.command().execute();
        assertBucketWritable(bucketCreator.waitUntilBucketExists());
    }

    @Test
    void testCreatedBucketWithDefaultValues() throws BucketAccessException, TimeoutException, InterruptedException {
        final BucketCreator bucketCreator = bucketCreator().assumeJsonRpcAvailable();
        assertDoesNotThrow(() -> bucketCreator.commandWithDefaultValues().execute());
    }

    // -----------------------------------------------------

    private BucketCreator bucketCreator() {
        return new BucketCreator(CreateBucketCommandIT.class, EXASOL);
    }

    private void assertBucketWritable(final SyncAwareBucket bucket)
            throws BucketAccessException, TimeoutException, InterruptedException {
        final var fileName = "test-uploaded.txt";
        bucket.uploadStringContent("file content", fileName);
        assertThat(bucket.listContents(), hasItem(fileName));
    }
}
