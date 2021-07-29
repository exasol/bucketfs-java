package com.exasol.bucketfs.uploadnecassity;

/**
 * This class converts a byte array into a hex representation.
 */
class ByteArrayToHexConverter {
    private ByteArrayToHexConverter() {
        // this class has only static methods
    }

    /**
     * Converts a byte array into a hex representation.
     *
     * @param bytes bytes to convert
     * @return hex representation as string
     */
    static String toHex(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (final byte checksumByte : bytes) {
            sb.append(String.format("%02x", checksumByte));
        }
        return sb.toString();
    }
}
