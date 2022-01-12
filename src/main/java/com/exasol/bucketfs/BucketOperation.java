package com.exasol.bucketfs;

/**
 * Types of operations a bucket supports.
 */
public enum BucketOperation {
    /**
     * download
     */
    DOWNLOAD,
    /**
     * list
     */
    LIST,
    /**
     * upload
     */
    UPLOAD,
    /**
     * delete
     */
    DELETE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}