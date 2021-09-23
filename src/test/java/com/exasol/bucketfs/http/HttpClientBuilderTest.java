package com.exasol.bucketfs.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.net.http.HttpClient;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpClientBuilderTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testBuildWithRaisingTlsErrors(final boolean raiseTlsErrors) {
        final HttpClient client = new HttpClientBuilder() //
                .raiseTlsErrors(raiseTlsErrors) //
                .build();
        assertThat(client, notNullValue());
    }
}
