package com.exasol.bucketfs;

import static com.exasol.bucketfs.testutil.ExceptionAssertions.assertThrowsWithMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.*;
import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class ReadEnabledBucketTest {

    private static final String BUCKET_NAME = "bucket";
    private static final String BUCKET_FS_NAME = "service";
    private static final String IP_ADDRESS = "101.102.103.104";
    private static final int PORT = 1234;

    @Mock
    private HttpClientBuilder httpClientBuilderMock;
    @Mock
    private HttpClient httpClientMock;
    @Mock
    private HttpResponse<Object> httpResponseMock;

    @CsvSource({
            "false, localhost, 8888, s1, b1, s1/b1 (http://localhost:8888)",
            "true, example.org, 1234, s2, b2, s2/b2 (https://example.org:1234)",
            "false, 127.0.0.1, 4242, , the_bucket, bfsdefault/the_bucket (http://127.0.0.1:4242)"
    })
    @ParameterizedTest
    void testToString(final boolean useTls, final String host, final int port, final String serviceName,
            final String bucketName, final String expected) {
        final var bucket = bucketBuilder()
                .useTls(useTls)
                .host(host)
                .port(port)
                .serviceName(serviceName)
                .name(bucketName).build();
        assertThat(bucket.toString(), equalTo(expected));
    }

    private ReadEnabledBucket.Builder<? extends Builder<?>> bucketBuilder() {
        return new Builder<>(this.httpClientBuilderMock);
    }

    @Test
    void testGetFullyQualifiedBucketName() {
        final var bucket = createBucket();

        assertThat(bucket.getFullyQualifiedBucketName(), equalTo(BUCKET_FS_NAME + "/" + BUCKET_NAME));
    }

    @Test
    void testGetFullyQualifiedBucketNameForDefaultServiceName() {
        final var bucket = createBucketWithDefaultServiceName();

        assertThat(bucket.getFullyQualifiedBucketName(), equalTo(BucketConstants.DEFAULT_BUCKETFS + "/" + BUCKET_NAME));
    }

    private ReadOnlyBucket createBucket() {
        return bucketBuilder().host(IP_ADDRESS) //
                .port(PORT) //
                .serviceName(BUCKET_FS_NAME) //
                .name(BUCKET_NAME) //
                .build();
    }

    private ReadOnlyBucket createBucketWithDefaultServiceName() {
        return bucketBuilder().host(IP_ADDRESS) //
                .port(PORT) //
                .name(BUCKET_NAME) //
                .build();
    }

    @Test
    // [utest->dsn~bucket-lists-files-with-common-prefix~1]
    void listRoot() throws Exception {
        simulateResponse(lines("dir/b1.txt", "b.txt", "dir/a1.txt", "a.txt"), 200);
        final List<String> actual = createBucket().listContents();
        assertThat(actual, equalTo(List.of("a.txt", "b.txt", "dir/")));
    }

    @Test
    // [utest->dsn~bucket-lists-files-with-common-prefix~1]
    void listSubDirectory() throws Exception {
        simulateResponse(lines("dir/b1.txt", "b.txt", "dir/a1.txt", "a.txt"), 200);
        final List<String> actual = createBucket().listContents("dir/");
        assertThat(actual, equalTo(List.of("a1.txt", "b1.txt")));
    }

    @Test
    // [utest->dsn~bucket-lists-file-and-directory-with-identical-name~1]
    // [utest->dsn~bucket-lists-directories-with-suffix~1]
    void listFileAndDirectoryWithIdenticalName() throws Exception {
        simulateResponse(lines("name", "name/child.txt"), 200);
        final List<String> actual = createBucket().listContents();
        assertThat(actual, equalTo(List.of("name", "name/")));
    }

    @Test
    // [utest->dsn~get-the-udf-bucket-path~1]
    void getBucketPathInUdf() {
        final ReadOnlyBucket bucket = createBucket();
        final String pathInUdf = bucket.getPathInUdf();
        assertThat(pathInUdf, equalTo("/buckets/service/bucket"));
    }

    @Test
    // [utest->dsn~get-the-udf-bucket-path~1]
    void testGetFilePathInUdf() {
        final ReadOnlyBucket bucket = createBucket();
        final String pathInUdf = bucket.getPathInUdf("my-file.txt");
        assertThat(pathInUdf, equalTo("/buckets/service/bucket/my-file.txt"));
    }

    private String lines(final String... lines) {
        return String.join("\n", lines);
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
            "404, E-BFSJ-2: File or directory not found trying to list 'http://101.102.103.104:1234/bucket/'.",
            "403, E-BFSJ-3: Access denied trying to list 'http://101.102.103.104:1234/bucket/'.",
            "500, E-BFSJ-1: Unable to perform list 'http://101.102.103.104:1234/bucket/'. HTTP status 500." })
    @ParameterizedTest
    void testRequestListingThrowsException(final int responseStatus, final String expectedExceptionMessage)
            throws IOException, InterruptedException {
        simulateResponse(null, responseStatus);
        final ReadOnlyBucket bucket = createBucket();
        assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                expectedExceptionMessage);
    }

    @Test
    void testRequestListingFailsWithIOException() throws IOException, InterruptedException {
        when(this.httpClientBuilderMock.build()).thenReturn(this.httpClientMock);
        when(this.httpClientMock.send(any(), any())).thenThrow(new IOException("expected"));

        final ReadOnlyBucket bucket = createBucket();
        assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                "E-BFSJ-5: I/O error trying to list 'http://101.102.103.104:1234/bucket/'");
    }

    @Test
    void testRequestListingFailsWithInterruptedException() throws IOException, InterruptedException {
        when(this.httpClientBuilderMock.build()).thenReturn(this.httpClientMock);
        when(this.httpClientMock.send(any(), any())).thenThrow(new InterruptedException("expected"));

        final ReadOnlyBucket bucket = createBucket();
        assertThrowsWithMessage(BucketAccessException.class, bucket::listContents,
                "E-BFSJ-4: Interrupted trying to list 'http://101.102.103.104:1234/bucket/'.");
    }

    @CsvSource({ //
            "true, https", //
            "false, http" //
    })
    @ParameterizedTest
    // [utest->dsn~tls-configuration~1]
    void testUseTlsUsesCorrectProtocol(final boolean useTls, final String protocol)
            throws BucketAccessException, IOException, InterruptedException {
        simulateResponse("", 200);

        bucketBuilder().useTls(useTls) //
                .host(IP_ADDRESS) //
                .port(PORT) //
                .serviceName(BUCKET_FS_NAME) //
                .name(BUCKET_NAME) //
                .build() //
                .listContents();

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

    @Test
    void buildSucceedsWithMinimalConfiguration() {
        assertThat(ReadEnabledBucket.builder().host("host").serviceName("service").name("bucket").build(),
                notNullValue());
    }

    @Test
    void buildFailsWithoutHost() {
        final Builder<?> builder = ReadEnabledBucket.builder().serviceName("service").name("bucket");
        assertThrowsWithMessage(NullPointerException.class, builder::build, "host");
    }

    @Test
    void buildFailsWithoutBucketName() {
        final Builder<?> builder = ReadEnabledBucket.builder().host("host").serviceName("service");
        assertThrowsWithMessage(NullPointerException.class, builder::build, "bucketName");
    }
}
