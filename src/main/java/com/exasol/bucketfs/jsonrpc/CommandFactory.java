package com.exasol.bucketfs.jsonrpc;

import static com.exasol.errorreporting.ExaError.messageBuilder;

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
 * Create commands that can be executed against the Exasol RPC interface.
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
     * Create a new builder for {@link CreateBucketCommand}.
     *
     * @return a new builder for {@link CreateBucketCommand}
     */
    // [impl->dsn~creating-new-bucket~1]
    public CreateBucketCommandBuilder makeCreateBucketCommand() {
        return CreateBucketCommand.builder(this.executor, this.jsonMapper);
    }

    /**
     * Create a new builder for {@link CommandFactory}.
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

        private boolean raiseTlsErrors = true;
        private URI serviceUri;
        private Authenticator authenticator;

        private Builder() {
            // empty by intention
        }

        /**
         * Define if TLS errors should raise an error when executing requests or if they should be ignored. Setting this
         * to <code>false</code> is required as the docker-db uses a self-signed certificate.
         * <p>
         * Defaults to raise TLS errors.
         *
         * @return this instance for method chaining
         */
        public Builder raiseTlsErrors(final boolean raise) {
            this.raiseTlsErrors = raise;
            return this;
        }

        /**
         * Set the URL of the RPC interface, e.g. {@code "https://<hostname>:443/jrpc"}.
         *
         * @param serverUrl RPC interface URL
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
            } catch (final URISyntaxException exception) {
                throw new IllegalArgumentException(
                        messageBuilder("E-BFSJ-19").message("Error parsing server URL {{serverUrl}}", uri)
                                .mitigation("Use a valid format for the URL").toString(),
                        exception);
            }
        }

        /**
         * Authenticate via the given bearer token.
         *
         * @param token bearer token
         * @return this instance for method chaining
         */
        public Builder bearerTokenAuthentication(final String token) {
            this.authenticator = new BearerTokenAuthenticator(token);
            return this;
        }

        /**
         * Authenticate via basic authentication.
         *
         * @param username username for authenticating against the RPC interface
         * @param password password for authenticating against the RPC interface
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
            } catch (final KeyManagementException exception) {
                throw new IllegalStateException(messageBuilder("E-BFSJ-20").message(
                        "Unable to initialize TLS context while trying to create HTTP client for RPC communication.")
                        .toString(), exception);
            }
        }

        private Optional<TrustManager[]> createSslTrustManagers() {
            if (this.raiseTlsErrors) {
                return Optional.empty();
            } else {
                return Optional.of(new TrustManager[] { new DummyTrustManager() });
            }
        }

        private SSLContext createSslContext() {
            try {
                return SSLContext.getInstance("TLS");
            } catch (final NoSuchAlgorithmException exception) {
                throw new IllegalStateException(messageBuilder("E-BFSJ-21").message(
                        "Unable to initialize TLS context while trying to create HTTP client for RPC communication.")
                        .toString(), exception);
            }
        }
    }
}
