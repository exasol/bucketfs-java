package com.exasol.bucketfs.http;

import static com.exasol.errorreporting.ExaError.messageBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Logger;

import javax.net.ssl.*;

import com.exasol.bucketfs.http.SubjectAltName.Type;
import com.exasol.bucketfs.jsonrpc.CommandFactory;

/**
 * A builder for {@link HttpClient} that provides a convenient way for ignoring TLS errors.
 */
public class HttpClientBuilder {
    private static final Logger LOGGER = Logger.getLogger(HttpClientBuilder.class.getName());

    private boolean raiseTlsErrors = true;
    private X509Certificate certificate;
    private final List<SubjectAltName> altNames = new ArrayList<>();

    /**
     * Define if TLS errors should raise an error when executing requests or if they should be ignored. Setting this to
     * <code>false</code> is required as the docker-db uses a self-signed certificate.
     * <p>
     * Defaults to raise TLS errors.
     * <p>
     * Setting this to {@code false} is mutually exclusive with {@link #certificate}.
     *
     * @param raiseTlsErrors <code>true</code> if the {@link CommandFactory} should fail for TLS errors,
     *                       <code>false</code> if it should ignore TLS errors.
     * @return this instance for method chaining
     */
    public HttpClientBuilder raiseTlsErrors(final boolean raiseTlsErrors) {
        this.raiseTlsErrors = raiseTlsErrors;
        return this;
    }

    /**
     * Use the given certificate for TLS connections.
     * <p>
     * Defaults to using the certificates from the JVMs default key store.
     * <p>
     * Mutually exclusive with setting {@link #raiseTlsErrors} to {@code false}.
     *
     * @param certificate certificate to use
     * @return this instance for method chaining
     */
    public HttpClientBuilder certificate(final X509Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    /**
     * Update the certificate specified via {@link #certificate(X509Certificate)} to allow an additional host name, e.g.
     * {@code localhost}.
     * <p>
     * This is useful when a self-signed certificate does not contain the required subject alternative name (SAN).
     * 
     * @param hostName additional hostname to allow
     * @return this instance for method chaining
     */
    public HttpClientBuilder allowAlternativeHostName(final String hostName) {
        return this.allowAltName(new SubjectAltName(Type.HOSTNAME, hostName));
    }

    /**
     * Update the certificate specified via {@link #certificate(X509Certificate)} to allow an additional IP address,
     * e.g. {@code 127.0.0.1}.
     * <p>
     * This is useful when a self-signed certificate does not contain the required subject alternative name (SAN).
     * 
     * @param ipAddress additional IP address to allow
     * @return this instance for method chaining
     */
    public HttpClientBuilder allowAlternativeIPAddress(final String ipAddress) {
        return this.allowAltName(new SubjectAltName(Type.IP, ipAddress));
    }

    private HttpClientBuilder allowAltName(final SubjectAltName altName) {
        this.altNames.add(altName);
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
            sslContext.init(null, createTrustManagers().orElse(null), null);
        } catch (final KeyManagementException exception) {
            throw new IllegalStateException(messageBuilder("E-BFSJ-20").message(
                    "Unable to initialize TLS context while trying to create HTTP client for RPC communication.")
                    .toString(), exception);
        }
    }

    private Optional<TrustManager[]> createTrustManagers() {
        if (!this.raiseTlsErrors && this.certificate != null) {
            throw new IllegalStateException(messageBuilder("E-BFSJ-27")
                    .message("Setting raiseTlsErrors to false and using a certificate is mutually exclusive.")
                    .mitigation("Either set raiseTlsErrors to true or remove the certificate.").toString());
        }
        if (!this.raiseTlsErrors) {
            return Optional.of(createDummyTrustManagers());
        } else if (this.certificate != null) {
            return Optional.of(createTrustManagerForCertificate()).map(this::allowAlternativeNames);
        } else {
            return Optional.empty();
        }
    }

    private TrustManager[] createDummyTrustManagers() {
        return new TrustManager[] { new DummyTrustManager() };
    }

    private TrustManager[] createTrustManagerForCertificate() {
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("caCert", this.certificate);
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            LOGGER.finest(() -> "Created " + trustManagers.length + " trust managers for certificate with subject '"
                    + this.certificate.getSubjectX500Principal() + "': " + Arrays.toString(trustManagers));
            return trustManagers;
        } catch (final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException exception) {
            throw new IllegalStateException(messageBuilder("E-BFSJ-25")
                    .message("Unable to create trust manager for given certificate").toString());
        }
    }

    // [impl->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
    private TrustManager[] allowAlternativeNames(final TrustManager[] trustManagers) {
        return Arrays.stream(trustManagers).map(this::allowAlternativeNames).toArray(TrustManager[]::new);
    }

    private TrustManager allowAlternativeNames(final TrustManager trustManager) {
        return SubjectAltNameTrustManager.wrap(trustManager, altNames);
    }

    private SSLContext createSslContext() {
        try {
            return SSLContext.getInstance("TLS");
        } catch (final NoSuchAlgorithmException exception) {
            throw new IllegalStateException(messageBuilder("E-BFSJ-26").message(
                    "Unable to initialize TLS context while trying to create HTTP client for RPC communication.")
                    .toString(), exception);
        }
    }
}
