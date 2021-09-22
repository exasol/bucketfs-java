package com.exasol.bucketfs.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;

import java.net.Socket;

import javax.net.ssl.SSLEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DummyTrustManagerTest {

    private DummyTrustManager trustManager;

    @BeforeEach
    void setUp() {
        this.trustManager = new DummyTrustManager();
    }

    @Test
    void testGetAcceptedIssuers() {
        assertThat(this.trustManager.getAcceptedIssuers(), arrayWithSize(0));
    }

    @Test
    void testCheckServerTrustedX509CertificateArrayString() {
        this.trustManager.checkServerTrusted(null, null);
    }

    @Test
    void testCheckClientTrustedX509CertificateArrayString() {
        this.trustManager.checkClientTrusted(null, null);
    }

    @Test
    void testCheckServerTrustedX509CertificateArrayStringSSLEngine() {
        this.trustManager.checkServerTrusted(null, null, (SSLEngine) null);
    }

    @Test
    void testCheckServerTrustedX509CertificateArrayStringSocket() {
        this.trustManager.checkServerTrusted(null, null, (Socket) null);
    }

    @Test
    void testCheckClientTrustedX509CertificateArrayStringSSLEngine() {
        this.trustManager.checkClientTrusted(null, null, (SSLEngine) null);
    }

    @Test
    void testCheckClientTrustedX509CertificateArrayStringSocket() {
        this.trustManager.checkClientTrusted(null, null, (Socket) null);
    }
}
