package com.exasol.bucketfs.uploadnecessity;

import java.nio.file.Path;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;

/**
 * This is an interface for strategies that decide if it necessary to upload a file to BucketFS.
 */
public interface UploadNecessityCheckStrategy {

    /**
     * Decide if it is necessary to upload a file to BucketFS.
     * 
     * @param file                   local file
     * @param fullFileNameInBucketFs path to the file in BucketFS
     * @param bucket                 bucket the file is uploaded to
     * @return {@code true} if the file should get uploaded
     * @throws BucketAccessException if the necessity check cannot be executed
     */
    public boolean isUploadNecessary(Path file, String fullFileNameInBucketFs, ReadOnlyBucket bucket)
            throws BucketAccessException;
}
