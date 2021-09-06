package com.exasol.bucketfs.jsonrpc.auth;

import java.net.http.HttpRequest;

public class BearerTokenAuthenticator implements Authenticator {

    private final String token;

    public BearerTokenAuthenticator(final String token) {
        this.token = token;
    }

    @Override
    public void authenticate(final HttpRequest.Builder requestBuilder) {
        requestBuilder.header("Authorization", "Bearer " + this.token);
    }
}
