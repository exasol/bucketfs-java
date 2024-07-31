package com.exasol.bucketfs.http;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.http.SubjectAltName.Type;

@ExtendWith(MockitoExtension.class)
class SubjectAltNameTrustManagerTest {

    private static final SubjectAltName ALT_NAME_HOST = new SubjectAltName(Type.HOSTNAME, "host");
    private static final SubjectAltName ALT_NAME_IP = new SubjectAltName(Type.IP, "ipAddr");

    @Mock
    private X509ExtendedTrustManager delegateMock;

    @Test
    void wrapFailsForUnsupportedType(@Mock final TrustManager genericTrustManager) {
        final List<SubjectAltName> altNames = List.of(ALT_NAME_HOST);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SubjectAltNameTrustManager.wrap(genericTrustManager, altNames));
        assertThat(exception.getMessage(), matchesPattern("TrustManager of type .* is not supported"));
    }

    @Test
    void wrapReturnsUnmodified(@Mock final TrustManager genericTrustManager) {
        assertThat(SubjectAltNameTrustManager.wrap(genericTrustManager, emptyList()),
                sameInstance(genericTrustManager));
    }

    @Test
    void wrapReturnsModifiedTrustManager() {
        assertThat(SubjectAltNameTrustManager.wrap(delegateMock, List.of(ALT_NAME_IP)),
                instanceOf(SubjectAltNameTrustManager.class));
    }

    @Test
    void addAlternativeNamesNullChain() throws CertificateException {
        assertThat(replaceCertificate(null), nullValue());
    }

    @Test
    void addAlternativeNamesEmptyChain() throws CertificateException {
        assertThat(replaceCertificate(new X509Certificate[0]), emptyArray());
    }

    @Test
    void addAlternativeNamesSingleElementChain(@Mock final X509Certificate cert1Mock) throws CertificateException {
        assertThat(replaceCertificate(new X509Certificate[] { cert1Mock }),
                arrayContaining(instanceOf(FixedSANCertificate.class)));
    }

    @Test
    void addAlternativeNamesMultiElementChain(@Mock final X509Certificate cert1Mock,
            @Mock final X509Certificate cert2Mock) throws CertificateException {
        assertThat(replaceCertificate(new X509Certificate[] { cert1Mock, cert2Mock }),
                arrayContaining(instanceOf(FixedSANCertificate.class), sameInstance(cert2Mock)));
    }

    private X509Certificate[] replaceCertificate(final X509Certificate[] chain) throws CertificateException {
        testee(ALT_NAME_HOST).checkServerTrusted(chain, "authType");
        final ArgumentCaptor<X509Certificate[]> arg = ArgumentCaptor.forClass(X509Certificate[].class);
        verify(delegateMock).checkServerTrusted(arg.capture(), same("authType"));
        return arg.getValue();
    }

    @Test
    void checkServerTrusted(@Mock final X509Certificate cert1Mock) throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee(ALT_NAME_HOST).checkServerTrusted(chain, "authType");
        verify(delegateMock).checkServerTrusted(any(), eq("authType"));
    }

    @Test
    void checkServerTrustedSocket(@Mock final X509Certificate cert1Mock, @Mock final Socket socket)
            throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee(ALT_NAME_HOST).checkServerTrusted(chain, "authType", socket);
        verify(delegateMock).checkServerTrusted(any(), eq("authType"), same(socket));
    }

    @Test
    void checkServerTrustedSslEngine(@Mock final X509Certificate cert1Mock, @Mock final SSLEngine engine)
            throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee(ALT_NAME_HOST).checkServerTrusted(chain, "authType", engine);
        verify(delegateMock).checkServerTrusted(any(), eq("authType"), same(engine));
    }

    @Test
    void checkClientTrusted(@Mock final X509Certificate cert1Mock) throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee().checkClientTrusted(chain, "authType");
        verify(delegateMock).checkClientTrusted(same(chain), eq("authType"));
    }

    @Test
    void checkClientTrustedSocket(@Mock final X509Certificate cert1Mock, @Mock final Socket socket)
            throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee().checkClientTrusted(chain, "authType", socket);
        verify(delegateMock).checkClientTrusted(same(chain), eq("authType"), same(socket));
    }

    @Test
    void checkClientTrustedSslEngine(@Mock final X509Certificate cert1Mock, @Mock final SSLEngine engine)
            throws CertificateException {
        final X509Certificate[] chain = new X509Certificate[] { cert1Mock };
        testee().checkClientTrusted(chain, "authType", engine);
        verify(delegateMock).checkClientTrusted(same(chain), eq("authType"), same(engine));
    }

    @Test
    void getAcceptedIssuers(@Mock final X509Certificate cert1Mock, @Mock final SSLEngine engine)
            throws CertificateException {
        final X509Certificate[] issuers = new X509Certificate[0];
        when(delegateMock.getAcceptedIssuers()).thenReturn(issuers);
        assertThat(testee().getAcceptedIssuers(), sameInstance(issuers));
    }

    X509ExtendedTrustManager testee(final SubjectAltName... altNames) {
        return (X509ExtendedTrustManager) SubjectAltNameTrustManager.wrap(delegateMock, asList(altNames));
    }
}
