package com.exasol.bucketfs;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import com.exasol.bucketfs.uploadnecessity.UploadNecessityCheckStrategy;

/**
 * Interface for write access to a bucket in Bucket FS.
 *
 * <p>
 * Note that the methods in this interface provide no guarantee that the written objects are synchronized across the
 * cluster when they return. If you require safe synchronization, use the {@link Bucket} interface and its
 * implementations instead.
 * </p>
 */
public interface UnsynchronizedBucket extends ReadOnlyBucket {
    /**
     * Types of archive that BucketFS can expand automatically.
     */
    public static final Set<String> SUPPORTED_ARCHIVE_EXTENSIONS = Set.of(".tar", ".tgz", ".tar.gz", ".zip");

    /**
     * Get the write password for the bucket.
     *
     * @return write password.
     */
    public String getWritePassword();

    /**
     * Upload a file to the bucket.
     * <p>
     * Upload a file from a given local path to a URI pointing to a BucketFS bucket. If the bucket URI ends in a slash,
     * that URI is interpreted as a directory inside the bucket and the original filename is appended.
     * </p>
     * <p>
     * This call immediately returns without checking whether or not the file is actually synchronized in the in
     * BucketFS.
     * </p>
     *
     * @param localPath    path of the file to be uploaded
     * @param pathInBucket path inside the bucket
     * @throws TimeoutException      if the synchronization check takes too long
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws FileNotFoundException in case the source file is not found
     * @return {@link UploadResult} describing the status of the upload
     */
    public UploadResult uploadFileNonBlocking(Path localPath, String pathInBucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException;

    /**
     * Upload the contents of a string to the bucket.
     * <p>
     * This method is intended for writing small objects in BucketFS dynamically like for example configuration files.
     * For large payload use {@link UnsynchronizedBucket#uploadFileNonBlocking(Path, String)} instead.
     * </p>
     * <p>
     * This call immediately returns without checking whether or not the file is actually synchronized in the in
     * BucketFS.
     * </p>
     *
     * @param content      string to write
     * @param pathInBucket path inside the bucket
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    public void uploadStringContentNonBlocking(String content, String pathInBucket)
            throws BucketAccessException, TimeoutException;

    /**
     * Upload the contents of an input stream to the bucket non-blocking.
     *
     * @param inputStreamSupplier supplier that provides the input stream
     * @param pathInBucket        path inside the bucket
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    // [impl->dsn~uploading-input-stream-to-bucket~1]
    public void uploadInputStreamNonBlocking(Supplier<InputStream> inputStreamSupplier, String pathInBucket)
            throws BucketAccessException, TimeoutException;

    /**
     * Delete a file from BucketFS.
     * <p>
     * Warning: If you try to upload a file shortly (less than about 30s) after you deleted it, the upload will fail
     * with access denied.
     * </p>
     *
     * @param pathInBucket file path
     * @throws BucketAccessException if delete failed
     */
    // [impl->dsn~delete-a-file-from-a-bucket~1]
    public void deleteFileNonBlocking(final String pathInBucket) throws BucketAccessException;

    /**
     * Set an {@link UploadNecessityCheckStrategy}.
     * <p>
     * This class uses the strategy during file upload to decide if the file should really be uploaded.
     * </p>
     * 
     * @param uploadNecessityCheckStrategy strategy to use from now on
     */
    public void setUploadNecessityCheckStrategy(final UploadNecessityCheckStrategy uploadNecessityCheckStrategy);
}