package com.exasol.bucketfs.uploadnecessity;

/**
 * This class converts a byte array into a hexadecimal representation.
 */
final class ByteArrayToHexConverter {
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
        final StringBuilder result = new StringBuilder();
        for (final byte eachByte : bytes) {
            result.append(String.format("%02x", eachByte));
        }
        return result.toString();
    }
}
