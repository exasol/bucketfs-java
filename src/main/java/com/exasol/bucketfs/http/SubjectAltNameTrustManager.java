package com.exasol.bucketfs.http;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.*;

/**
 * This Trust Manager allows overwriting the subject alternative name in a certificate. This is required because Exasol
 * Docker DB uses a self-signed certificate that does not specify {@code localhost}.
 * <p>
 * Based on https://stackoverflow.com/a/77538035
 */
class SubjectAltNameTrustManager extends X509ExtendedTrustManager {
    private static final Logger LOGGER = Logger.getLogger(SubjectAltNameTrustManager.class.getName());

    private final X509ExtendedTrustManager delegate;
    private final List<SubjectAltName> alternativeNames;

    private SubjectAltNameTrustManager(final X509ExtendedTrustManager delegate, final List<SubjectAltName> altNames) {
        this.delegate = delegate;
        this.alternativeNames = altNames;
    }

    // [impl->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
    static TrustManager wrap(final TrustManager delegate, final List<SubjectAltName> altNames) {
        if (altNames.isEmpty()) {
            return delegate;
        }
        if (delegate instanceof X509ExtendedTrustManager) {
            LOGGER.info(() -> "Allow additional subject alternative names (SAN) " + altNames + " for certificate");
            return new SubjectAltNameTrustManager((X509ExtendedTrustManager) delegate, altNames);
        }
        throw new IllegalArgumentException(
                "TrustManager of type " + delegate.getClass().getName() + " is not supported");
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        delegate.checkServerTrusted(addAlternativeNames(chain), authType);
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {
        delegate.checkServerTrusted(addAlternativeNames(chain), authType, socket);
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {
        delegate.checkServerTrusted(addAlternativeNames(chain), authType, engine);
    }

    private X509Certificate[] addAlternativeNames(final X509Certificate[] chain) {
        if (chain == null || chain.length < 1) {
            return chain;
        }
        LOGGER.finest(() -> "Modify Subject Alternative Name of first certificate to use " + alternativeNames);
        final FixedSANCertificate fixedSANCertificate = new FixedSANCertificate(chain[0], alternativeNames);
        return replaceFirstCertificate(chain, fixedSANCertificate);
    }

    private X509Certificate[] replaceFirstCertificate(final X509Certificate[] chain,
            final FixedSANCertificate fixedSANCertificate) {
        final X509Certificate[] result = new X509Certificate[chain.length];
        System.arraycopy(chain, 0, result, 0, chain.length);
        result[0] = fixedSANCertificate;
        return result;
    }

    // Methods below only delegate

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        delegate.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {
        delegate.checkClientTrusted(chain, authType, socket);
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {
        delegate.checkClientTrusted(chain, authType, engine);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return delegate.getAcceptedIssuers();
    }
}
