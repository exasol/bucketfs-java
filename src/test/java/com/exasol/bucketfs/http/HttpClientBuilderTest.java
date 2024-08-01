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
    // [utest->dsn~custom-tls-certificate~1]
    void testBuildWithCertificate() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(mock(X509Certificate.class));
        assertThat(builder.build(), notNullValue());
    }

    @Test
    // [utest->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
    void testBuildWithAltHostName() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(mock(X509Certificate.class)) //
                .allowAlternativeHostName("altHost");
        assertThat(builder.build(), notNullValue());
    }

    @Test
    // [utest->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
    void testBuildWithAltIpAddress() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(mock(X509Certificate.class)) //
                .allowAlternativeIPAddress("altIpAddr");
        assertThat(builder.build(), notNullValue());
    }

    @Test
    void testBuildWithAltHostNameWithoutCertificateFails() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(null) //
                .allowAlternativeHostName("altHost");
        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class, builder::build,
                "E-BFSJ-31: Using alternative subject names requires configuring a certificate. Either specify a certificate or remove the alternative subject names.");
    }

    @Test
    void testBuildWithAltIpAddressWithoutCertificateFails() {
        final HttpClientBuilder builder = new HttpClientBuilder() //
                .raiseTlsErrors(true) //
                .certificate(null) //
                .allowAlternativeIPAddress("altIpAddr");
        ExceptionAssertions.assertThrowsWithMessage(IllegalStateException.class, builder::build,
                "E-BFSJ-31: Using alternative subject names requires configuring a certificate. Either specify a certificate or remove the alternative subject names.");
    }
}
