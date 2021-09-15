package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;

import org.junit.jupiter.api.Test;

class BasicAuthAuthenticatorTest {

    @Test
    void testAuthenticate() throws URISyntaxException {
        final var requestBuilder = HttpRequest.newBuilder(new URI("http://localhost"));

        new BasicAuthAuthenticator("user", "password").authenticate(requestBuilder);

        assertAuthHeader(requestBuilder, "Basic dXNlcjpwYXNzd29yZA==");
    }

    private void assertAuthHeader(final Builder requestBuilder, final String expectedAuthHeader) {
        final var request = requestBuilder.build();
        final var authHeaders = request.headers().allValues("Authorization");
        assertThat(authHeaders, contains(expectedAuthHeader));
    }
}
