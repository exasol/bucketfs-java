package com.exasol.bucketfs.jsonrpc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.net.http.HttpRequest.Builder;

/**
 * Authorization assertions
 */
public class AuthorizationAssertions {
    private AuthorizationAssertions() {
        // Not instantiable
    }

    static void assertAuthHeader(final Builder requestBuilder, final String expectedAuthHeader) {
        final var request = requestBuilder.build();
        final var authHeaders = request.headers().allValues("Authorization");
        assertThat(authHeaders, contains(expectedAuthHeader));
    }
}
