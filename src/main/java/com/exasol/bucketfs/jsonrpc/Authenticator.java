package com.exasol.bucketfs.jsonrpc;

import java.net.http.HttpRequest;

/**
 * Implemented by classes that add authentication to an HTTP request, e.g. for bearer token authentication or basic
 * authentication.
 */
interface Authenticator {

    /**
     * Authenticate the given request builder e.g. by adding an {@code Authorization} header.
     *
     * @param requestBuilder request to authorize.
     */
    public void authenticate(HttpRequest.Builder requestBuilder);
}
