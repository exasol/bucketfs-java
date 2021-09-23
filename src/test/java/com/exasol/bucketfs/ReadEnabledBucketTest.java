package com.exasol.bucketfs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.ReadEnabledBucket.Builder;
import com.exasol.bucketfs.http.HttpClientBuilder;
import com.exasol.bucketfs.testutil.ExceptionAssertions;

@ExtendWith(MockitoExtension.class)
class ReadEnabledBucketTest {

    private static final String BUCKET_NAME = "bucket";
    private static final String BUCKET_FS_NAME = "service";
    private static final String IP_ADDRESS = "192.168.1.1";
    private static final int PORT = 1234;

    @Mock
    private HttpClientBuilder httpClientBuilderMock;
    @Mock
    private HttpClient httpClientMock;
    @Mock
    private HttpResponse<Object> httpResponseMock;

    @CsvSource({ //
            "1234, service, bucket, service/bucket", //
            "1234,        , bucket, 1234:bucket" //
    })
    @ParameterizedTest
    void toString(final int port, final String serviceName, final String bucketName, final String expectedOutput) {
        final var bucket = bucketBuilder() //
                .ipAddress(IP_ADDRESS) //
                .httpPort(port) //
                .serviceName(serviceName) //
                .name(bucketName).build();
        assertThat(bucket.toString(), equalTo(expectedOutput));
    }

    private ReadEnabledBucket.Builder<? extends Builder<?>> bucketBuilder() {
        return new Builder<>(this.httpClientBuilderMock);
    }

    @Test
    void testGetFullyQualifiedBucketName() {
        final var bucket = createBucket();

        assertThat(bucket.getFullyQualifiedBucketName(), equalTo(BUCKET_FS_NAME + "/" + BUCKET_NAME));
    }

    private ReadOnlyBucket createBucket() {
        return bucketBuilder().ipAddress(IP_ADDRESS) //
                .httpPort(PORT) //
                .serviceName(BUCKET_FS_NAME) //
                .name(BUCKET_NAME) //
                .build();
    }

    @Test
    void testRequestListingSucceeds() throws BucketAccessException, IOException, InterruptedException {
        simulateResponse("file", 200);

        assertThat(createBucket().listContents(), contains("file"));
    }

    private void simulateResponse(final String responseBody, final int responseStatus)
            throws IOException, InterruptedException {
        when(this.httpClientBuilderMock.build()).thenReturn(this.httpClientMock);
        when(this.httpClientMock.send(any(), any())).thenReturn(this.httpResponseMock);
        if (responseBody != null) {
            when(this.httpResponseMock.body()).thenReturn(responseBody);
        }
        when(this.httpResponseMock.statusCode()).thenReturn(responseStatus);
    }

    @CsvSource({ //
            "404, 'E-BFSJ-2: File or directory not found trying to list http://192.168.1.1:1234/bucket/.'",
            "403, 'E-BFSJ-3: Access denied trying to list http://192.168.1.1:1234/bucket/.'",
            "500, 'E-BFSJ-1: Unable do list http://192.168.1.1:1234/bucket/. HTTP status 500.'" })
    @ParameterizedTest
    void testRequestListingThrowsException(final int responseStatus, final String expectedExceptionMessage)
            throws BucketAccessException, IOException, InterruptedException {
        simulateResponse(null, responseStatus);

        final ReadOnlyBucket bucket = createBucket();
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                expectedExceptionMessage);
    }

    @Test
    void testRequestListingFailsWithIOException() throws BucketAccessException, IOException, InterruptedException {
        when(this.httpClientBuilderMock.build()).thenReturn(this.httpClientMock);
        when(this.httpClientMock.send(any(), any())).thenThrow(new IOException("expected"));

        final ReadOnlyBucket bucket = createBucket();
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                "E-BFSJ-5: I/O error trying to list http://192.168.1.1:1234/bucket/");
    }

    @Test
    void testRequestListingFailsWithInterruptedException()
            throws BucketAccessException, IOException, InterruptedException {
        when(this.httpClientBuilderMock.build()).thenReturn(this.httpClientMock);
        when(this.httpClientMock.send(any(), any())).thenThrow(new InterruptedException("expected"));

        final ReadOnlyBucket bucket = createBucket();
        ExceptionAssertions.assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                "E-BFSJ-4: Interrupted trying to list http://192.168.1.1:1234/bucket/.");
    }

    @CsvSource({ //
            "true, https", //
            "false, http" //
    })
    @ParameterizedTest
    void testUseTlsUsesCorrectProtocol(final boolean useTls, final String protocol)
            throws BucketAccessException, IOException, InterruptedException {
        simulateResponse("", 200);

        bucketBuilder().useTls(useTls).ipAddress(IP_ADDRESS).httpPort(PORT).serviceName(BUCKET_FS_NAME)
                .name(BUCKET_NAME).build().listContents();

        final ArgumentCaptor<HttpRequest> arg = ArgumentCaptor.forClass(HttpRequest.class);
        verify(this.httpClientMock).send(arg.capture(), any());
        assertThat(arg.getValue().uri().toString(),
                equalTo(protocol + "://" + IP_ADDRESS + ":" + PORT + "/" + BUCKET_NAME + "/"));
    }

    @ValueSource(booleans = { true, false })
    @ParameterizedTest
    void testRaiseTlsErrors(final boolean raiseTlsErrors) {
        bucketBuilder().raiseTlsErrors(raiseTlsErrors);

        verify(this.httpClientBuilderMock).raiseTlsErrors(raiseTlsErrors);
    }
}