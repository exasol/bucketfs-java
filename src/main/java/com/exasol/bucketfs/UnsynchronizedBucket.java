package com.exasol.bucketfs;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

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
    String getWritePassword();

    /***
     * Check if a synchronization monitor is registered for this Bucket.
     *
     * @return {@code true} if a monitor is available
     */
    boolean hasSynchronizationMonitor();

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
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @throws TimeoutException      if the synchronization check takes too long
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     */
    void uploadFileNonBlocking(Path localPath, String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException;

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
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    void uploadStringContentNonBlocking(String content, String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException;

    /**
     * Upload the contents of an input stream to the bucket.
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFS or a timeout occurs.
     * </p>
     *
     * @param inputStreamSupplier supplier that provides the input stream
     * @param pathInBucket        path inside the bucket
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    // [impl->dsn~uploading-input-stream-to-bucket~1]
    void uploadInputStream(Supplier<InputStream> inputStreamSupplier, String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException;

    /**
     * Upload the contents of an input stream to the bucket.
     * <p>
     * When blocking is enabled, this call waits until either the uploaded file is synchronized or a timeout occurred.
     * </p>
     *
     * @param inputStreamSupplier supplier that provides the input stream
     * @param pathInBucket        path inside the bucket
     * @param blocking            when set to {@code true}, the call waits for the uploaded object to be synchronized,
     *                            otherwise immediately returns
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    // [impl->dsn~dsn~uploading-input-stream-to-bucket~1]
    void uploadInputStream(Supplier<InputStream> inputStreamSupplier, String pathInBucket, boolean blocking)
            throws InterruptedException, BucketAccessException, TimeoutException;

}
