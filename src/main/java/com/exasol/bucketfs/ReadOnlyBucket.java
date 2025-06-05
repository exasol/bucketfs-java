package com.exasol.bucketfs;

import java.nio.file.Path;
import java.util.List;

import static com.exasol.bucketfs.BucketConstants.PATH_SEPARATOR;

/**
 * Interface for read-only bucket access.
 */
public interface ReadOnlyBucket {

    /**
     * Prefix for building UDF-visible paths to buckets in BucketFS.
     * <p>
     * In Exasol, UDFs see BucketFS under {@code /buckets/}. This constant helps build
     * correct file paths as seen from within UDFs.
     * </p>
     *
     * @see #getPathInUdf()
     * @see #getPathInUdf(String)
     */
    static final String PATH_IN_UDF_PREFIX = PATH_SEPARATOR + "buckets" + PATH_SEPARATOR;
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
     */
    List<String> listContents() throws BucketAccessException;

    /**
     * Recursively list the contents of a bucket.
     *
     * @return bucket contents
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     */
    List<String> listContentsRecursively() throws BucketAccessException;

    /**
     * List the contents of a path inside a bucket.
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     */
    List<String> listContents(String path) throws BucketAccessException;

    /**
     * Recursively list the contents of a path inside a bucket.
     *
     * @param path relative path from the bucket root
     * @return list of file system entries
     * @throws BucketAccessException if the contents are not accessible or the path is invalid
     */
    List<String> listContentsRecursively(String path) throws BucketAccessException;

    /**
     * Download a file from a bucket to a local filesystem.
     *
     * @param pathInBucket path of the file in BucketFS
     * @param localPath    local path the file is downloaded to
     * @throws BucketAccessException if the local file does not exist or is not accessible or if the download failed
     */
    void downloadFile(String pathInBucket, Path localPath) throws BucketAccessException;

    /**
     * Download a file from a bucket into a string.
     *
     * @param pathInBucket path of the file in BucketFS
     * @return file contents as string
     * @throws BucketAccessException if the local file does not exist or is not accessible or if the download failed
     */
    String downloadFileAsString(String pathInBucket) throws BucketAccessException;

    /**
     * Return the UDF-visible path to the root of this bucket within BucketFS.
     * <p>
     * This method ensures consistency and avoids human error by generating the correct chrooted path
     * as seen from within a User-Defined Function (UDF) environment.
     * </p>
     *
     * <p>
     * In Exasol, BucketFS is the only accessible filesystem for UDFs and it operates in a chrooted
     * environment. As such, paths inside UDFs differ from those on the host system or exposed via
     * the BucketFS web interface. This method abstracts away those differences and provides
     * the correct UDF-local path.
     * </p>
     *
     * @return the absolute path to the bucket as seen inside the UDF
     */
    // [impl->dsn~get-the-udf-bucket-path~1]
    default String getPathInUdf() {
        return PATH_IN_UDF_PREFIX + getBucketFsName() + PATH_SEPARATOR + getBucketName();
    }

    /**
     * Return the UDF-visible path to a specific file within this bucket in BucketFS.
     * <p>
     * This method ensures that the path to the given file is correctly formed in the context
     * of a UDF environment, taking into account the chrooted nature of BucketFS.
     * </p>
     *
     * <p>
     * This is useful for referencing files inside a bucket from UDFs, where the apparent file
     * paths are isolated from the underlying host filesystem.
     * </p>
     *
     * @param fileInBucketFs the relative path or name of the file inside the bucket
     * @return the absolute path to the file as seen inside the UDF
     */
    // [impl->dsn~get-the-udf-bucket-path~1]
    default String getPathInUdf(final String fileInBucketFs) {
        return getPathInUdf() + PATH_SEPARATOR + fileInBucketFs;
    }
}