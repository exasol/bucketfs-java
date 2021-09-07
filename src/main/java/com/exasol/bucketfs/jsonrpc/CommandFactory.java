package com.exasol.bucketfs.jsonrpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.security.*;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;

public class CommandFactory {
    private final JsonMapper jsonMapper;
    private final JsonRpcCommandExecutor executor;

    private CommandFactory(final JsonRpcCommandExecutor executor, final JsonMapper jsonMapper) {
        this.executor = executor;
        this.jsonMapper = jsonMapper;
    }

    public CreateBucketCommandBuilder makeCreateBucketCommand() {
        return CreateBucketCommand.builder(this.executor, this.jsonMapper);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean ignoreSslErrors = false;
        private URI serviceUri;
        private Authenticator authenticator;

        private Builder() {

        }

        public Builder ignoreSslErrors() {
            this.ignoreSslErrors = true;
            return this;
        }

        public Builder serverUrl(final String serverUrl) {
            this.serviceUri = parseUri(serverUrl);
            return this;
        }

        public Builder bearerTokenAuthentication(final String token) {
            this.authenticator = new BearerTokenAuthenticator(token);
            return this;
        }

        public Builder basicAuthentication(final String username, final String password) {
            this.authenticator = new BasicAuthAuthenticator(username, password);
            return this;
        }

        private URI parseUri(final String uri) {
            try {
                return new URI(uri);
            } catch (final URISyntaxException e) {
                throw new IllegalArgumentException("Error parsing uri '" + uri + "'", e);
            }
        }

        public CommandFactory build() {
            final JsonMapper jsonMapper = JsonMapper.create();
            final HttpClient httpClient = createHttpClient();
            final JsonRpcClient client = new JsonRpcClient(httpClient, jsonMapper, this.authenticator, this.serviceUri);
            final JsonRpcCommandExecutor executor = new JsonRpcCommandExecutor(client, jsonMapper);

            return new CommandFactory(executor, jsonMapper);
        }

        private HttpClient createHttpClient() {
            final SSLContext sslContext = createSslContext();
            initializeSslContext(sslContext);
            return HttpClient.newBuilder().sslContext(sslContext).build();
        }

        private void initializeSslContext(final SSLContext sslContext) {
            try {
                sslContext.init(null, createSslTrustManagers().orElse(null), new SecureRandom());
            } catch (final KeyManagementException e) {
                throw new IllegalStateException("Error initializing ssl context", e);
            }
        }

        private Optional<TrustManager[]> createSslTrustManagers() {
            if (this.ignoreSslErrors) {
                return Optional.of(new TrustManager[] { new DummyTrustManager() });
            }
            return Optional.empty();
        }

        private SSLContext createSslContext() {
            try {
                return SSLContext.getInstance("TLS");
            } catch (final NoSuchAlgorithmException e) {
                throw new IllegalStateException("Error creating ssl context", e);
            }
        }
    }
}
