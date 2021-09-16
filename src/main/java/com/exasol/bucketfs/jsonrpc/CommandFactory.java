package com.exasol.bucketfs.jsonrpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.exasol.bucketfs.jsonrpc.CreateBucketCommand.CreateBucketCommandBuilder;

/**
 * Creates commands that can be executed against the Exasol RPC interface.
 *
 * Create new instances by creating a builder using {@link #builder()}.
 * <p>
 * The following fields are required:
 * <ul>
 * <li>{@link Builder#serverUrl(String)}</li>
 * <li>One of the two authentication methods:
 * <ul>
 * <li>{@link Builder#basicAuthentication(String, String)} or</li>
 * <li>{@link Builder#bearerTokenAuthentication(String)}</li>
 * </ul>
 * </li>
 * </ul>
 */
public class CommandFactory {
    private final JsonMapper jsonMapper;
    private final JsonRpcCommandExecutor executor;

    private CommandFactory(final JsonRpcCommandExecutor executor, final JsonMapper jsonMapper) {
        this.executor = executor;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Creates a new builder for {@link CreateBucketCommand}.
     *
     * @return a new builder for {@link CreateBucketCommand}
     */
    // [impl->dsn~creating-new-bucket~1]
    public CreateBucketCommandBuilder makeCreateBucketCommand() {
        return CreateBucketCommand.builder(this.executor, this.jsonMapper);
    }

    /**
     * Creates a new builder for {@link CommandFactory}.
     *
     * @return a new builder for {@link CommandFactory}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for a {@link CommandFactory}.
     * <p>
     * Call {@link CommandFactory#builder()} to create a new instance.
     */
    public static class Builder {

        private boolean ignoreSslErrors = false;
        private URI serviceUri;
        private Authenticator authenticator;

        private Builder() {
            // empty by intention
        }

        /**
         * Ignore all SSL errors when executing requests. This is required as the docker-db uses a self-signed
         * certificate.
         * <p>
         * Defaults to <em>not</em> ignoring SSL errors if not called.
         *
         * @return this instance for method chaining
         */
        public Builder ignoreSslErrors() {
            this.ignoreSslErrors = true;
            return this;
        }

        /**
         * Sets the URL of the RPC interface, e.g. {@code "https://<hostname>:443/jrpc"}.
         *
         * @param serverUrl the RPC interface URL
         * @return this instance for method chaining
         * @throws IllegalArgumentException the URL has an invalid format
         */
        public Builder serverUrl(final String serverUrl) {
            this.serviceUri = parseUri(serverUrl);
            return this;
        }

        private URI parseUri(final String uri) {
            try {
                return new URI(uri);
            } catch (final URISyntaxException e) {
                throw new IllegalArgumentException("Error parsing uri '" + uri + "'", e);
            }
        }

        /**
         * Authenticate via the given bearer token.
         *
         * @param token the bearer token
         * @return this instance for method chaining
         */
        public Builder bearerTokenAuthentication(final String token) {
            this.authenticator = new BearerTokenAuthenticator(token);
            return this;
        }

        /**
         * Authenticate via basic authentication.
         *
         * @param username the username
         * @param password the password
         * @return this instance for method chaining
         */
        public Builder basicAuthentication(final String username, final String password) {
            this.authenticator = new BasicAuthAuthenticator(username, password);
            return this;
        }

        /**
         * Build a new {@link CommandFactory}.
         *
         * @return the new {@link CommandFactory}
         * @throws NullPointerException in case not all mandatory fields where defined
         */
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
                sslContext.init(null, createSslTrustManagers().orElse(null), null);
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
