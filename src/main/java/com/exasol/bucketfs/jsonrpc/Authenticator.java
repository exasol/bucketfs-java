package com.exasol.bucketfs.jsonrpc;

import java.net.http.HttpRequest;

interface Authenticator {
    void authenticate(HttpRequest.Builder requestBuilder);
}
