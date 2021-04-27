package com.exasol.bucketfs;

/**
 * Types of operations a bucket supports.
 */
public enum BucketOperation {
    DOWNLOAD, LIST, UPLOAD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}