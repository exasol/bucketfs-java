package com.exasol.bucketfs.uploadnecassity;

import java.nio.file.Path;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.bucketfs.ReadOnlyBucket;

/**
 * This is an interface for strategies that decide if it necessary to upload a file to BucketFs.
 */
//[impl->dsn~conditional-upload~1]
public interface UploadNecessityCheckStrategy {

    /**
     * Decide if it's necessary to upload a file to BucketFs.
     * 
     * @param file                   local file
     * @param fullFileNameInBucketFs path to the file in BucketFs
     * @param bucket                 bucket the file is uploaded to
     * @return {@code true} if the file should get uploaded
     * @throws BucketAccessException if checking fails
     */
    public boolean isUploadNecessary(Path file, String fullFileNameInBucketFs, ReadOnlyBucket bucket)
            throws BucketAccessException;
}
