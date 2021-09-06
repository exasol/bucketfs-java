package com.exasol.bucketfs.jsonrpc;

import java.net.http.HttpClient;
import java.security.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class HttpClientFactory {

    public HttpClient createHttpClient() {
        final SSLContext sslContext = createSslContext();
        final HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
        return client;
    }

    private SSLContext createSslContext() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Error creating ssl context", e);
        }
    }
}
