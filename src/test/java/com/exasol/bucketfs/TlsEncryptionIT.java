package com.exasol.bucketfs;

import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKET;
import static com.exasol.bucketfs.BucketConstants.DEFAULT_BUCKETFS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;

import com.exasol.bucketfs.ReadEnabledBucket.Builder;

class TlsEncryptionIT extends AbstractBucketIT {

    @BeforeEach
    void assertTlsSupported() {
        assumeTrue(dbUsesTls(), "Current Exasol container does not support TLS for BucketFS");
    }

    @Nested
    class CertificateAddedToClient {
        @Test
        // [itest->dsn~custom-tls-certificate~1]
        // [itest->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
        // [itest->dsn~tls-configuration~1]
        void tlsConnectionWorks() throws BucketAccessException {
            final ReadOnlyBucket bucket = createBucket(true, getHost(), getHost(), null);
            assertConnectionWorks(bucket);
        }

        @Test
        // [itest->dsn~tls-configuration~1]
        void unencryptedConnectionFails() {
            final ReadOnlyBucket bucket = createBucket(false, getHost(), getHost(), null);
            assertConnectionError(bucket, equalTo("HTTP/1.1 header parser received no bytes"));
        }

        @Test
        void connectionWithWrongAltNameFails() {
            final ReadOnlyBucket bucket = createBucket(true, getHost(), "wrongAltName", null);
            assertConnectionError(bucket, equalTo("No subject alternative DNS name matching " + getHost() + " found."));
        }

        @Test
        // [itest->dsn~custom-tls-certificate.additional-subject-alternative-names~1]
        void connectionWithIpAddressWithAltNameWorks() throws BucketAccessException {
            assumeDockerLocalhost();
            final ReadOnlyBucket bucket = createBucket(true, "127.0.0.1", null, "127.0.0.1");
            assertConnectionWorks(bucket);
        }

        @Test
        void connectionWithIpAddressWithoutAltNameFails() {
            assumeDockerLocalhost();
            final ReadOnlyBucket bucket = createBucket(true, "127.0.0.1", null, null);
            assertConnectionError(bucket, equalTo("No subject alternative names present"));
        }

        @Test
        void connectionWithIpAddressWithWrongAltNameFails() {
            assumeDockerLocalhost();
            final ReadOnlyBucket bucket = createBucket(true, "127.0.0.1", null, "wrongAltIp");
            assertConnectionError(bucket, equalTo("No subject alternative names matching IP address 127.0.0.1 found"));
        }

        private ReadOnlyBucket createBucket(final boolean dbUsesTls, final String host,
                final String alternativeHostName, final String alternativeIpAddress) {
            final Builder<? extends Builder<?>> builder = ReadEnabledBucket.builder() //
                    .host(host) //
                    .port(getMappedDefaultBucketFsPort()) //
                    .useTls(dbUsesTls) //
                    .certificate(getDbCertificate()) //
                    .serviceName(DEFAULT_BUCKETFS) //
                    .name(DEFAULT_BUCKET) //
                    .readPassword(getDefaultBucketConfiguration().getReadPassword());
            if (alternativeHostName != null) {
                builder.allowAlternativeHostName(alternativeHostName);
            }
            if (alternativeIpAddress != null) {
                builder.allowAlternativeIpAddress(alternativeIpAddress);
            }
            return builder.build();
        }
    }

    @Nested
    class NoCertificateAddedToClient {

        @Test
        void unencryptedConnectionFails() {
            final ReadOnlyBucket bucket = createBucket(false, getHost());
            assertConnectionError(bucket, equalTo("HTTP/1.1 header parser received no bytes"));
        }

        @Test
        void encryptedConnectionFails() {
            final ReadOnlyBucket bucket = createBucket(true, getHost());
            assertConnectionError(bucket, equalTo(
                    "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target"));
        }

        @Test
        void encryptedConnectionWithIpFails() {
            assumeDockerLocalhost();
            final ReadOnlyBucket bucket = createBucket(true, "127.0.0.1");
            assertConnectionError(bucket, equalTo(
                    "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target"));
        }

        private ReadOnlyBucket createBucket(final boolean dbUsesTls, final String host) {
            final Builder<? extends Builder<?>> builder = ReadEnabledBucket.builder() //
                    .host(host) //
                    .port(getMappedDefaultBucketFsPort()) //
                    .useTls(dbUsesTls) //
                    .certificate(null) //
                    .serviceName(DEFAULT_BUCKETFS) //
                    .name(DEFAULT_BUCKET) //
                    .readPassword(getDefaultBucketConfiguration().getReadPassword());
            return builder.build();
        }
    }

    private void assertConnectionWorks(final ReadOnlyBucket bucket) throws BucketAccessException {
        assertThat(bucket.listContents(), hasSize(greaterThanOrEqualTo(0)));
    }

    private void assertConnectionError(final ReadOnlyBucket bucket, final Matcher<String> expectedCauseMessage) {
        final BucketAccessException exception = assertThrows(BucketAccessException.class, bucket::listContents);
        assertAll(() -> assertThat(exception.getMessage(), startsWith("E-BFSJ-5: I/O error trying to list 'http")),
                () -> assertThat(exception.getCause(), not(nullValue())),
                () -> assertThat(exception.getCause().getMessage(), expectedCauseMessage));
    }

    private void assumeDockerLocalhost() {
        assumeTrue(getHost().equals("localhost"), "Docker container running on localhost");
    }
}
