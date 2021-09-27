package com.exasol.bucketfs.http;

import java.net.Socket;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * A trust manager that ignores all SSL errors. This is required because the docker-db uses a self signed certificate.
 */
@SuppressWarnings("java:S4830") // Disabling certificate validation by intention
final class DummyTrustManager extends X509ExtendedTrustManager {
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
        // empty by intention
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        // empty by intention
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
        // empty by intention
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
        // empty by intention
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
        // empty by intention
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
        // empty by intention
    }
}