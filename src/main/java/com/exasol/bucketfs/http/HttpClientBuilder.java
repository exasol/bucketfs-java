package com.exasol.bucketfs.http;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.exasol.bucketfs.jsonrpc.CommandFactory;

/**
 * A builder for {@link HttpClient} that provides a convenient way for ignoring TLS errors.
 */
public class HttpClientBuilder {

    private boolean raiseTlsErrors = true;

    /**
     * Define if TLS errors should raise an error when executing requests or if they should be ignored. Setting this to
     * <code>false</code> is required as the docker-db uses a self-signed certificate.
     * <p>
     * Defaults to raise TLS errors.
     *
     * @param raise <code>true</code> if the {@link CommandFactory} should fail for TLS errors, <code>false</code> if it
     *              should ignore TLS errors.
     * @return this instance for method chaining
     */
    public HttpClientBuilder raiseTlsErrors(final boolean raiseTlsErrors) {
        this.raiseTlsErrors = raiseTlsErrors;
        return this;
    }

    /**
     * Creates a new {@link HttpClient} using the specified configuration.
     *
     * @return a new {@link HttpClient}
     */
    public HttpClient build() {
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
