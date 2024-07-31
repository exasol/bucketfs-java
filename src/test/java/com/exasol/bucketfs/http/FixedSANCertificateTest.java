package com.exasol.bucketfs.http;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.bucketfs.http.SubjectAltName.Type;

@ExtendWith(MockitoExtension.class)
class FixedSANCertificateTest {
    @Mock
    X509Certificate delegateMock;

    @Test
    void getSubjectAlternativeNamesEmpty() throws CertificateParsingException {
        when(delegateMock.getSubjectAlternativeNames()).thenReturn(List.of(List.of("a", "b")));
        assertThat(testee().getSubjectAlternativeNames(), contains(List.of("a", "b")));
    }

    @Test
    void getSubjectAlternativeNamesDelegateReturnsNull() throws CertificateParsingException {
        when(delegateMock.getSubjectAlternativeNames()).thenReturn(null);
        assertThat(testee().getSubjectAlternativeNames(), empty());
    }

    @Test
    void getSubjectAlternativeNames() throws CertificateParsingException {
        when(delegateMock.getSubjectAlternativeNames()).thenReturn(List.of(List.of("a", "b")));
        assertThat(
                testee(new SubjectAltName(Type.HOSTNAME, "host"), new SubjectAltName(Type.IP, "ipAddr"))
                        .getSubjectAlternativeNames(),
                contains(List.of("a", "b"), List.of(2, "host"), List.of(7, "ipAddr")));
    }

    @Test
    void hasUnsupportedCriticalExtension() {
        when(delegateMock.hasUnsupportedCriticalExtension()).thenReturn(true);
        assertThat(testee().hasUnsupportedCriticalExtension(), is(true));
    }

    @Test
    void getCriticalExtensionOIDs() {
        when(delegateMock.getCriticalExtensionOIDs()).thenReturn(Set.of("a", "b"));
        assertThat(testee().getCriticalExtensionOIDs(), equalTo(Set.of("a", "b")));
    }

    @Test
    void getNonCriticalExtensionOIDs() {
        when(delegateMock.getNonCriticalExtensionOIDs()).thenReturn(Set.of("a", "b"));
        assertThat(testee().getNonCriticalExtensionOIDs(), equalTo(Set.of("a", "b")));
    }

    @Test
    void getExtensionValue() {
        when(delegateMock.getExtensionValue("oid")).thenReturn(new byte[] { 1, 2, 3 });
        assertThat(testee().getExtensionValue("oid"), equalTo(new byte[] { 1, 2, 3 }));
    }

    @Test
    void getVersion() {
        when(delegateMock.getVersion()).thenReturn(42);
        assertThat(testee().getVersion(), equalTo(42));
    }

    @Test
    void getSerialNumber() {
        when(delegateMock.getSerialNumber()).thenReturn(BigInteger.valueOf(42));
        assertThat(testee().getSerialNumber(), equalTo(BigInteger.valueOf(42)));
    }

    @Test
    void getIssuerDN(@Mock final Principal issuerDn) {
        when(delegateMock.getIssuerDN()).thenReturn(issuerDn);
        assertThat(testee().getIssuerDN(), sameInstance(issuerDn));
    }

    @Test
    void getSubjectDN(@Mock final Principal subjectDn) {
        when(delegateMock.getSubjectDN()).thenReturn(subjectDn);
        assertThat(testee().getSubjectDN(), sameInstance(subjectDn));
    }

    @Test
    void getNotBefore() {
        final Date date = new Date();
        when(delegateMock.getNotBefore()).thenReturn(date);
        assertThat(testee().getNotBefore(), sameInstance(date));
    }

    @Test
    void getNotAfter() {
        final Date date = new Date();
        when(delegateMock.getNotAfter()).thenReturn(date);
        assertThat(testee().getNotAfter(), sameInstance(date));
    }

    @Test
    void getTBSCertificate() throws CertificateEncodingException {
        when(delegateMock.getTBSCertificate()).thenReturn(new byte[] { 1, 2, 3 });
        assertThat(testee().getTBSCertificate(), equalTo(new byte[] { 1, 2, 3 }));
    }

    @Test
    void getSignature() {
        when(delegateMock.getSignature()).thenReturn(new byte[] { 1, 2, 3 });
        assertThat(testee().getSignature(), equalTo(new byte[] { 1, 2, 3 }));
    }

    @Test
    void getSigAlgName() {
        when(delegateMock.getSigAlgName()).thenReturn("alg");
        assertThat(testee().getSigAlgName(), equalTo("alg"));
    }

    @Test
    void getSigAlgOID() {
        when(delegateMock.getSigAlgOID()).thenReturn("alg");
        assertThat(testee().getSigAlgOID(), equalTo("alg"));
    }

    @Test
    void getSigAlgParams() {
        when(delegateMock.getSigAlgParams()).thenReturn(new byte[] { 1, 2, 3 });
        assertThat(testee().getSigAlgParams(), equalTo(new byte[] { 1, 2, 3 }));
    }

    @Test
    void getIssuerUniqueID() {
        when(delegateMock.getIssuerUniqueID()).thenReturn(new boolean[] { true, false });
        assertThat(testee().getIssuerUniqueID(), equalTo(new boolean[] { true, false }));
    }

    @Test
    void getSubjectUniqueID() {
        when(delegateMock.getSubjectUniqueID()).thenReturn(new boolean[] { true, false });
        assertThat(testee().getSubjectUniqueID(), equalTo(new boolean[] { true, false }));
    }

    @Test
    void getKeyUsage() {
        when(delegateMock.getKeyUsage()).thenReturn(new boolean[] { true, false });
        assertThat(testee().getKeyUsage(), equalTo(new boolean[] { true, false }));
    }

    @Test
    void getBasicConstraints() {
        when(delegateMock.getBasicConstraints()).thenReturn(42);
        assertThat(testee().getBasicConstraints(), equalTo(42));
    }

    @Test
    void getEncoded() throws CertificateEncodingException {
        when(delegateMock.getEncoded()).thenReturn(new byte[] { 1, 2, 3 });
        assertThat(testee().getEncoded(), equalTo(new byte[] { 1, 2, 3 }));
    }

    @Test
    void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        testee().checkValidity();
        Mockito.verify(delegateMock).checkValidity();
    }

    @Test
    void checkValidityDate() throws CertificateExpiredException, CertificateNotYetValidException {
        final Date date = new Date();
        testee().checkValidity(date);
        Mockito.verify(delegateMock).checkValidity(same(date));
    }

    @Test
    void verify(@Mock final PublicKey key) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        testee().verify(key);
        Mockito.verify(delegateMock).verify(same(key));
    }

    @Test
    void verifySigProvider(@Mock final PublicKey key) throws InvalidKeyException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        testee().verify(key, "SigProvider");
        Mockito.verify(delegateMock).verify(same(key), eq("SigProvider"));
    }

    @Test
    void getPublicKey(@Mock final PublicKey key) {
        when(delegateMock.getPublicKey()).thenReturn(key);
        assertThat(testee().getPublicKey(), sameInstance(key));
    }

    @Test
    void testToString() {
        when(delegateMock.toString()).thenReturn("toString");
        assertThat(testee().toString(), equalTo("toString"));
    }

    @Test
    void testEquals(@Mock final X509Certificate other) {
        // This violates the "equals" contract but is required here.
        // equals() can't be mocked.
        assertThat(testee().equals(other), is(false));
        assertThat(testee().equals(delegateMock), is(true));
    }

    @Test
    void testHashCode() {
        // This violates the "hashCode" contract but is required here
        assertThat(testee().hashCode(), equalTo(delegateMock.hashCode()));
    }

    X509Certificate testee(final SubjectAltName... altNames) {
        return new FixedSANCertificate(delegateMock, asList(altNames));
    }
}
