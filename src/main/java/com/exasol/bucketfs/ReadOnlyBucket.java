package com.exasol.bucketfs;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for read-only bucket access.
 */
public interface ReadOnlyBucket {
    /**
     * @return name of the BucketFS filesystem this bucket belongs to
     */
    String getBucketFsName();

    /**
     * @return name of the bucket
     */
    String getBucketName();

    /**
     * Get the fully qualified name of the bucket.
     *
     * @return fully qualified name consisting of service name and bucket name
     */
    String getFullyQualifiedBucketName();

    /**
     * Get the read password for the bucket.
     *
     * @return read password
     */
    String getReadPassword();

    /**
     * List the contents of a bucket.
     *
     * @return bucket contents
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    List<String> listContents() throws BucketAccessException, InterruptedException;

    /**
     * List the contents of a path inside a bucket.
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     * @throws InterruptedException  if the list request was interrupted
     */
    List<String> listContents(String path) throws BucketAccessException, InterruptedException;

    /**
     * Download a file from a bucket to a local filesystem.
     *
     * @param pathInBucket path of the file in BucketFS
     * @param localPath    local path the file is downloaded to
     * @throws InterruptedException  if the file download was interrupted
     * @throws BucketAccessException if the local file does not exist or is not accessible or if the download failed
     */
    void downloadFile(String pathInBucket, Path localPath) throws InterruptedException, BucketAccessException;

    /**
     * Download a file from a bucket into a string.
     *
     * @param pathInBucket path of the file in BucketFS
     * @throws InterruptedException  if the file download was interrupted
     * @throws BucketAccessException if the local file does not exist or is not accessible or if the download failed
     */
    String downloadFileAsString(String pathInBucket) throws InterruptedException, BucketAccessException;
}