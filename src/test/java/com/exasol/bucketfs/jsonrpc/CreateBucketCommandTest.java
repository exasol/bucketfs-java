package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.jsonrpc.AbstractJsonResponseCommand.JsonRpcResponse;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.Request;

@ExtendWith(MockitoExtension.class)
class CreateBucketCommandTest {
    @Mock
    private JsonRpcCommandExecutor commandExcecutorMock;
    private final JsonMapper jsonMapper = JsonMapper.create();

    @Test
    void testExecutionFailsForMissingBucketFsName() {
        assertCreateBucketCommandThrowsExceptionWithMessage(builder(), NullPointerException.class, "bucketFsName");
    }

    private CreateBucketCommandBuilder builder() {
        return CreateBucketCommand.builder(this.commandExcecutorMock, this.jsonMapper);
    }

    private void assertCreateBucketCommandThrowsExceptionWithMessage(final CreateBucketCommandBuilder builder,
            final Class<? extends Throwable> expectedException, final String expectedMessage) {
        final Throwable exception = assertThrows(expectedException, builder::execute);
        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void testExecutionFailsForMissingBucketName() {
        assertCreateBucketCommandThrowsExceptionWithMessage(builder().bucketFsName("bfs"), NullPointerException.class,
                "bucketName");
    }

    @Test
    void testExecutionWithDefaultValues() {
        builder().bucketFsName("bfs").bucketName("bucket").execute();

        final Request request = getRpcRequest();
        assertThat(request.getBucketFsName(), equalTo("bfs"));
        assertThat(request.getBucketName(), equalTo("bucket"));
        assertThat(request.isPublic(), equalTo(false));
        assertThat(request.getReadPassword(), nullValue());
        assertThat(request.getWritePassword(), nullValue());
    }

    private Request getRpcRequest() {
        final ArgumentCaptor<CreateBucketCommand> arg = ArgumentCaptor.forClass(CreateBucketCommand.class);
        verify(this.commandExcecutorMock).execute(arg.capture());
        assertThat(arg.getValue().getJobName(), equalTo("bucket_add"));
        return arg.getValue().getParameters();
    }

    @Test
    void testExecutionWithCustomValues() {
        builder().bucketFsName("bfs").bucketName("bucket").isPublic(true).readPassword("read").writePassword("write")
                .execute();

        final Request request = getRpcRequest();
        assertThat(request.getBucketFsName(), equalTo("bfs"));
        assertThat(request.getBucketName(), equalTo("bucket"));
        assertThat(request.isPublic(), equalTo(true));
        assertThat(request.getReadPassword(), equalTo("cmVhZA=="));
        assertThat(request.getWritePassword(), equalTo("d3JpdGU="));
    }

    @Test
    void testExecutionFailsWithErrorDescription() {
        final CreateBucketCommandBuilder commandBuilder = builder().bucketFsName("bfs").bucketName("bucket");

        simulateResponse(0, "error name");

        assertExecutionFails(commandBuilder,
                "E-BFSJ-18: RPC command 'com.exasol.bucketfs.jsonrpc.CreateBucketCommand' failed, received error result Response [jobId=null, code=0, name=error name, description=null, revision=0, output=null] from server.");
    }

    @Test
    void testExecutionFailsWithErrorCode() {
        final CreateBucketCommandBuilder commandBuilder = builder().bucketFsName("bfs").bucketName("bucket");

        simulateResponse(1, "OK");

        assertExecutionFails(commandBuilder,
                "E-BFSJ-18: RPC command 'com.exasol.bucketfs.jsonrpc.CreateBucketCommand' failed, received error result Response [jobId=null, code=1, name=OK, description=null, revision=0, output=null] from server.");
    }

    @Test
    void testExecutionSucceeds() {
        final CreateBucketCommandBuilder commandBuilder = builder().bucketFsName("bfs").bucketName("bucket");

        simulateResponse(0, "OK");

        assertDoesNotThrow(commandBuilder::execute);
    }

    private void assertExecutionFails(final CreateBucketCommandBuilder commandBuilder,
            final String expectedExceptionMessage) {
        final JsonRpcException exception = assertThrows(JsonRpcException.class, commandBuilder::execute);
        assertThat(exception.getMessage(), equalTo(expectedExceptionMessage));
    }

    private void simulateResponse(final int code, final String name) {
        final JsonRpcResponse response = new JsonRpcResponse();
        response.setCode(code);
        response.setName(name);
        simulateResponse(response);
    }

    private void simulateResponse(final JsonRpcResponse response) {

        final String responsePayload = this.jsonMapper.toJsonObject(response).toString();

        when(this.commandExcecutorMock.execute(any())).thenAnswer(invocation -> {
            final CreateBucketCommand command = invocation.getArgument(0, CreateBucketCommand.class);
            command.processResult(responsePayload);
            return null;
        });
    }

    @Test
    void testSwitchOffBase64EncodingOfPasswords() {
        builder()
                .useBase64EncodedPasswords(false)
                .bucketFsName("bfs")
                .bucketName("bucket")
                .writePassword("write_cleartext")
                .readPassword("read_cleartext")
                .execute();
        final Request request = getRpcRequest();
        assertAll(
                ()->assertThat(request.getReadPassword(), equalTo("read_cleartext")),
                ()->assertThat(request.getWritePassword(), equalTo("write_cleartext"))
        );
    }
}
