package com.exasol.bucketfs.http;

import java.security.cert.X509Certificate;

class SubjectAltName {
    private final Type type;
    private final String value;

    SubjectAltName(final Type type, final String value) {
        this.type = type;
        this.value = value;
    }

    Type getType() {
        return type;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "X509SubjectAltName [type=" + type + ", value=" + value + "]";
    }

    /** SAN value types. Aligns with {@link X509Certificate#getSubjectAlternativeNames} */
    enum Type {
        HOSTNAME(2), IP(7);

        final int code;

        private Type(final int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
