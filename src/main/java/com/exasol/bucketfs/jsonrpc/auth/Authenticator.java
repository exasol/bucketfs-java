package com.exasol.bucketfs.jsonrpc.auth;

import java.net.http.HttpRequest;

public interface Authenticator {
    void authenticate(HttpRequest.Builder requestBuilder);
}
