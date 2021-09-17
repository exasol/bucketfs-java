package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.jsonrpc.JsonRpcCommandExecutor.JsonRpcPayload;

@ExtendWith(MockitoExtension.class)
class JsonRpcClientTest {

    private static final String SERVICE_URI = "http://localhost";
    private static final String RPC_METHOD = "method";
    private static final String RPC_JOB = "job";
    private static final Object RESPONSE_BODY = "responseBody";

    @Mock
    private Authenticator authenticatorMock;
    @Mock
    private HttpClient httpClientMock;
    @Mock
    private HttpResponse<Object> httpResponseMock;

    @Test
    void testSendRequestSucceedsForStatus200() throws URISyntaxException, IOException, InterruptedException {
        simulateResponse(200);

        assertDoesNotThrow(() -> sendRequest());
    }

    @Test
    void testSendRequestSucceedsForStatus250() throws URISyntaxException, IOException, InterruptedException {
        simulateResponse(250);

        assertDoesNotThrow(() -> sendRequest());
    }

    @Test
    void testSendRequestFailsWithErrorCode() throws URISyntaxException, IOException, InterruptedException {
        simulateResponse(404);

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> sendRequest());

        assertThat(exception.getMessage(), equalTo("E-BFSJ-22: RPC request " + SERVICE_URI
                + " POST failed with response code 404. Response body was '" + RESPONSE_BODY + "'"));
    }

    @Test
    void testSendRequestFailsWithIOException() throws URISyntaxException, IOException, InterruptedException {
        when(this.httpClientMock.send(any(), any())).thenThrow(new IOException("expected"));

        final JsonRpcException exception = assertThrows(JsonRpcException.class, () -> sendRequest());

        assertThat(exception.getMessage(),
                equalTo("E-BFSJ-23: Unable to execute RPC request " + SERVICE_URI + " POST"));
    }

    @Test
    void testSendRequestFailsWithInterruptedException() throws URISyntaxException, IOException, InterruptedException {
        when(this.httpClientMock.send(any(), any())).thenThrow(new InterruptedException("expected"));

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> sendRequest());

        assertThat(exception.getMessage(),
                equalTo("E-BFSJ-24: Interrupted when sending RPC request " + SERVICE_URI + " POST"));
    }

    private void sendRequest() throws URISyntaxException, IOException, InterruptedException {
        final JsonRpcClient jsonRpcClient = new JsonRpcClient(this.httpClientMock, JsonMapper.create(),
                this.authenticatorMock, new URI(SERVICE_URI));

        jsonRpcClient.sendRequest(new JsonRpcPayload(RPC_METHOD, RPC_JOB, null));
    }

    private void simulateResponse(final int responseStatusCode) throws IOException, InterruptedException {
        when(this.httpResponseMock.body()).thenReturn(RESPONSE_BODY);
        when(this.httpResponseMock.statusCode()).thenReturn(responseStatusCode);
        when(this.httpClientMock.send(any(), any())).thenReturn(this.httpResponseMock);
    }
}
