package com.exasol.bucketfs;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import com.exasol.bucketfs.monitor.BucketFsMonitor.State;

/**
 * Interface for accessing BucketFS buckets.
 */
public interface Bucket extends UnsynchronizedBucket {
    /**
     * Check if the object with the given path is marked as synchronized after a given point in time.
     * <p>
     * The timestamp helps telling subsequent synchronizations appart.
     * </p>
     *
     * @param pathInBucket path to the object inside the bucket
     * @param state        state after which the synchronization needs to happen.
     * @return {@code true} if the object is synchronized
     * @throws BucketAccessException if the object at the reference does not exist or is inaccessible
     */
    boolean isObjectSynchronized(String pathInBucket, State state) throws BucketAccessException;

    /**
     * Upload a file to the bucket.
     * <p>
     * Upload a file from a given local path to a URI pointing to a BucketFS bucket. If the bucket URI ends in a slash,
     * that URI is interpreted as a directory inside the bucket and the original filename is appended.
     * </p>
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFS or a timeout occurs.
     * </p>
     *
     * @param pathInBucket path inside the bucket
     * @param localPath    path of the file to be uploaded
     * @throws TimeoutException      if the synchronization check takes too long
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws FileNotFoundException if the source file is not found
     */
    // [impl->dsn~uploading-to-bucket~1]
    void uploadFile(Path localPath, String pathInBucket)
            throws BucketAccessException, TimeoutException, FileNotFoundException;

    /**
     * Upload the contents of a string to the bucket.
     * <p>
     * This method is intended for writing small objects in BucketFS dynamically like for example configuration files.
     * For large payload use {@link Bucket#uploadFile(Path, String)} instead.
     * </p>
     * <p>
     * This call blocks until the uploaded file is synchronized in BucketFS or a timeout occurs.
     * </p>
     *
     * @param content      string to write
     * @param pathInBucket path inside the bucket
     * @throws InterruptedException  if the upload is interrupted
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    // [impl->dsn~uploading-strings-to-bucket~1]
    void uploadStringContent(String content, String pathInBucket)
            throws InterruptedException, BucketAccessException, TimeoutException;

    /**
     * Upload the contents of an input stream to the bucket.
     *
     * @param inputStreamSupplier supplier that provides the input stream
     * @param pathInBucket        path inside the bucket
     * @throws BucketAccessException if the file cannot be uploaded to the given URI
     * @throws TimeoutException      if synchronization takes too long
     */
    void uploadInputStream(Supplier<InputStream> inputStreamSupplier, String pathInBucket)
            throws BucketAccessException, TimeoutException;
}
