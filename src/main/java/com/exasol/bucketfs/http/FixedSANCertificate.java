package com.exasol.bucketfs.http;

import static java.util.stream.Collectors.toList;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * This certificate delegates to another {@link X509Certificate} but modifies it to add additional Subject Alternative
 * Names (SAN).
 * <p>
 * Based on https://stackoverflow.com/a/77538035
 */
// [impl->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
@SuppressWarnings("serial") // No serialization required
class FixedSANCertificate extends X509Certificate {
    private final X509Certificate delegate;
    private final transient Collection<SubjectAltName> altNames;

    FixedSANCertificate(final X509Certificate delegate, final Collection<SubjectAltName> altNames) {
        this.delegate = delegate;
        this.altNames = altNames;
    }

    @Override
    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        final Stream<List<?>> existingAltNames = Optional.ofNullable(delegate.getSubjectAlternativeNames())
                .map(Collection::stream).orElseGet(Stream::empty);
        final Stream<List<Object>> additionalAltNames = altNames.stream()
                .map(altName -> List.of(altName.getType().getCode(), altName.getValue()));
        return Stream.concat(existingAltNames, additionalAltNames).collect(toList());
    }

    // Methods below just delegate

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return delegate.hasUnsupportedCriticalExtension();
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return delegate.getCriticalExtensionOIDs();
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return delegate.getNonCriticalExtensionOIDs();
    }

    @Override
    public byte[] getExtensionValue(final String oid) {
        return delegate.getExtensionValue(oid);
    }

    @Override
    public int getVersion() {
        return delegate.getVersion();
    }

    @Override
    public BigInteger getSerialNumber() {
        return delegate.getSerialNumber();
    }

    @Override
    public Principal getIssuerDN() {
        return delegate.getIssuerDN();
    }

    @Override
    public Principal getSubjectDN() {
        return delegate.getSubjectDN();
    }

    @Override
    public Date getNotBefore() {
        return delegate.getNotBefore();
    }

    @Override
    public Date getNotAfter() {
        return delegate.getNotAfter();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return delegate.getTBSCertificate();
    }

    @Override
    public byte[] getSignature() {
        return delegate.getSignature();
    }

    @Override
    public String getSigAlgName() {
        return delegate.getSigAlgName();
    }

    @Override
    public String getSigAlgOID() {
        return delegate.getSigAlgOID();
    }

    @Override
    public byte[] getSigAlgParams() {
        return delegate.getSigAlgParams();
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return delegate.getIssuerUniqueID();
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return delegate.getSubjectUniqueID();
    }

    @Override
    public boolean[] getKeyUsage() {
        return delegate.getKeyUsage();
    }

    @Override
    public int getBasicConstraints() {
        return delegate.getBasicConstraints();
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return delegate.getEncoded();
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        delegate.checkValidity();
    }

    @Override
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        delegate.checkValidity(date);
    }

    @Override
    public void verify(final PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchProviderException, SignatureException {
        delegate.verify(key);
    }

    @Override
    public void verify(final PublicKey key, final String sigProvider) throws CertificateException,
            NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        delegate.verify(key, sigProvider);
    }

    @Override
    public PublicKey getPublicKey() {
        return delegate.getPublicKey();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // Methods equal() and hashCode() must be defined like this, else it won't work.
    @Override
    public boolean equals(final Object other) {
        return delegate.equals(other);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
