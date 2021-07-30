package com.exasol.bucketfs;

/**
 * This class contains the result of an upload operation.
 */
public class UploadResult {
    private final boolean wasUploadNecessary;

    /**
     * Create a new instance of {@link UploadResult}.
     * 
     * @param wasUploadNecessary if the upload was necessary
     */
    UploadResult(final boolean wasUploadNecessary) {
        this.wasUploadNecessary = wasUploadNecessary;
    }

    /**
     * Get if the file was uploaded or skipped.
     * 
     * @return {@code true} if the file was uploaded. {@code false} if an upload was not necessary
     */
    public boolean wasUploadNecessary() {
        return this.wasUploadNecessary;
    }
}
