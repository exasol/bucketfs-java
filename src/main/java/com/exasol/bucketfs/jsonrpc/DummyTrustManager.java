package com.exasol.bucketfs.jsonrpc;

import java.net.Socket;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

@SuppressWarnings("java:S4830") // Disabling certificate validation by intention
final class DummyTrustManager extends X509ExtendedTrustManager {
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }

    @Override
    public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, final String authType) {
        // empty by intention
    }

    @Override
    public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType) {
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