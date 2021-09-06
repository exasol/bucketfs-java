package com.exasol.bucketfs.jsonrpc;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

final class DummyTrustManager extends X509ExtendedTrustManager {
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }

    @Override
    public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
            throws CertificateException {
    }

    @Override
    public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
            throws CertificateException {
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket)
            throws CertificateException {
    }
}