package com.exasol.bucketfs.jsonrpc;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Logger;

import com.exasol.bucketfs.jsonrpc.JsonRpcCommandExecutor.JsonRpcPayload;

class JsonRpcClient {
    private static final Logger LOGGER = Logger.getLogger(JsonRpcClient.class.getName());

    private final HttpClient httpClient;
    private final JsonMapper serializer;
    private final Authenticator authenticator;
    private final URI serviceUri;

    JsonRpcClient(final HttpClient httpClient, final JsonMapper serializer, final Authenticator authenticator,
            final URI serviceUri) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        this.serializer = Objects.requireNonNull(serializer, "serializer");
        this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
        this.serviceUri = Objects.requireNonNull(serviceUri, "serviceUri");
    }

    String sendRequest(final JsonRpcPayload payload) {
        final var request = buildRequest(payload);
        final Instant start = Instant.now();
        final HttpResponse<String> response = sendRequest(request);
        verifySuccessResponse(request, response);
        final String responseBody = response.body();
        LOGGER.fine(() -> "Received response " + response + " with body '" + responseBody + "' after "
                + Duration.between(start, Instant.now()));
        return responseBody;
    }

    private HttpRequest buildRequest(final JsonRpcPayload payload) {
        final String requestBody = this.serializer.serialize(payload);
        final Builder requestBuilder = HttpRequest.newBuilder(this.serviceUri)
                .POST(BodyPublishers.ofString(requestBody));
        this.authenticator.authenticate(requestBuilder);
        final var request = requestBuilder.build();
        LOGGER.fine(() -> "Sending request " + request + " with body '" + requestBody + "'");
        return request;
    }

    private void verifySuccessResponse(final HttpRequest request, final HttpResponse<String> response) {
        if ((response.statusCode() / 100) != 2) {
            final String message = "Received non-ok response status " + response.statusCode() + " for request "
                    + request + ". Response body: '" + response.body() + "'";
            LOGGER.warning(message);
            throw new JsonRpcException(message);
        }
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
