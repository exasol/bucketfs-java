package com.exasol.bucketfs.jsonrpc;

import java.net.http.HttpRequest.Builder;
import java.util.Base64;
import java.util.Base64.Encoder;

/**
 * Authenticate an HTTP request via basic auth and given username and password.
 */
class BasicAuthAuthenticator implements Authenticator {
    private final String username;
    private final String password;
    private final Encoder base64Encoder;

    BasicAuthAuthenticator(final String username, final String password) {
        this.username = username;
        this.password = password;
        this.base64Encoder = Base64.getEncoder();
    }

    @Override
    public void authenticate(final Builder requestBuilder) {
        requestBuilder.header("Authorization", basicAuth(this.username, this.password));
    }

    private String basicAuth(final String username, final String password) {
        return "Basic " + this.base64Encoder.encodeToString((username + ":" + password).getBytes());
    }
}
