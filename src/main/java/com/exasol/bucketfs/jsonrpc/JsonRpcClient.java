package com.exasol.bucketfs.jsonrpc;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;

import com.exasol.bucketfs.jsonrpc.auth.Authenticator;

public class JsonRpcClient {
    private static final Logger LOGGER = Logger.getLogger(JsonRpcClient.class.getName());

    private final HttpClient httpClient;
    private final JsonMapper serializer;
    private final Authenticator interceptor;
    private final URI serverUri;

    public JsonRpcClient(final HttpClient httpClient, final JsonMapper serializer,
            final Authenticator interceptor, final URI serverUri) {
        this.httpClient = httpClient;
        this.serializer = serializer;
        this.interceptor = interceptor;
        this.serverUri = serverUri;
    }

    public String sendRequest(final JsonRpcPayload payload) {
        final String requestBody = this.serializer.serialize(payload);
        final Builder requestBuilder = HttpRequest.newBuilder(this.serverUri)
                .POST(BodyPublishers.ofString(requestBody));
        this.interceptor.authenticate(requestBuilder);
        final var request = requestBuilder.build();
        LOGGER.fine(() -> "Sending request " + request + " with body '" + requestBody + "'");
        final HttpResponse<String> response = sendRequest(request);
        final String responseBody = response.body();
        if ((response.statusCode() / 100) != 2) {
            throw new JsonRpcException("Received non-ok response status " + response.statusCode() + " for request "
                    + request + ". Response body: '" + responseBody + "'");
        }
        LOGGER.fine(() -> "Received response " + response + " with body '" + responseBody + "'");
        return responseBody;
    }

    private HttpResponse<String> sendRequest(final HttpRequest request) {
        try {
            return this.httpClient.send(request, BodyHandlers.ofString());
        } catch (final IOException e) {
            throw new JsonRpcException("Error executing request '" + request + "'", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JsonRpcException("Error executing request '" + request + "'", e);
        }
    }

}
