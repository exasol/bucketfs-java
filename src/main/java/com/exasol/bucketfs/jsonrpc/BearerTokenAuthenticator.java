package com.exasol.bucketfs.jsonrpc;

import java.net.http.HttpRequest;

/**
 * Authenticate an HTTP request via a bearer token.
 */
class BearerTokenAuthenticator implements Authenticator {

    private final String token;

    BearerTokenAuthenticator(final String token) {
        this.token = token;
    }

    @Override
    public void authenticate(final HttpRequest.Builder requestBuilder) {
        requestBuilder.header("Authorization", "Bearer " + this.token);
    }
}
