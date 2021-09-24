package com.exasol.bucketfs.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.bucketfs.testutil.ExceptionAssertions;

class HttpClientBuilderTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testBuildWithRaisingTlsErrors(final boolean raiseTlsErrors) {
        final HttpClient client = new HttpClientBuilder() //
                .raiseTlsErrors(raiseTlsErrors) //
                .build();
        assertThat(client, notNullValue());
    }

    @Test
    void testBuildWithIgnoreTlsErrorsAndCertificate() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(false) //
                .certificate(mock(X509Certificate.class));
        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class, builder::build,
                "E-BFSJ-27: Setting raiseTlsErrors to false and using a certificate is mutually exclusive. Either set raiseTlsErrors to true or remove the certificate.");
    }

    @Test
    void testBuildWithCertificate() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(mock(X509Certificate.class));
        assertThat(builder.build(), notNullValue());
    }
}
