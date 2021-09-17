package com.exasol.bucketfs.jsonrpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import org.junit.jupiter.api.Test;

class BearerTokenAuthenticatorTest {

    @Test
    void testAuthenticate() throws URISyntaxException {
        final var requestBuilder = HttpRequest.newBuilder(new URI("http://localhost"));

        new BearerTokenAuthenticator("token").authenticate(requestBuilder);

        AuthorizationAssertions.assertAuthHeader(requestBuilder, "Bearer token");
    }
}
