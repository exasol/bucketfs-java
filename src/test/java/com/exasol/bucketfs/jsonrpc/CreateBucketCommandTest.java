package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;
import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.Request;

@ExtendWith(MockitoExtension.class)
class CreateBucketCommandTest {

    @Mock
    private JsonRpcCommandExecutor commandExcecutorMock;
    @Mock
    private JsonMapper jsonMapperMock;

    @Test
    void testExecutionFailsForMissingBucketFsName() {
        assertCreateBucketCommandThrowsExceptionWithMessage(builder(), NullPointerException.class, "bucketFsName");
    }

    @Test
    void testExecutionFailsForMissingBucketName() {
        assertCreateBucketCommandThrowsExceptionWithMessage(builder().bucketFsName("bfs"), NullPointerException.class, "bucketName");
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

    private Request getRpcRequest() {
        final ArgumentCaptor<CreateBucketCommand> arg = ArgumentCaptor.forClass(CreateBucketCommand.class);
        verify(this.commandExcecutorMock).execute(arg.capture());
        assertThat(arg.getValue().getJobName(), equalTo("bucket_add"));
        return arg.getValue().getParameters();
    }

    private void assertCreateBucketCommandThrowsExceptionWithMessage(final CreateBucketCommandBuilder builder,
            final Class<? extends Throwable> expectedException, final String expectedMessage) {
        final Throwable exception = assertThrows(expectedException, builder::execute);
        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    private CreateBucketCommandBuilder builder() {
        return CreateBucketCommand.builder(this.commandExcecutorMock, this.jsonMapperMock);
    }
}
