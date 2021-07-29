package com.exasol.bucketfs.uploadnecessity;

import java.nio.file.Path;

import com.exasol.bucketfs.ReadOnlyBucket;

/**
 * This {@link UploadNecessityCheckStrategy} always decides to upload the file.
 */
public class UploadAlwaysStrategy implements UploadNecessityCheckStrategy {
    @Override
    public boolean isUploadNecessary(final Path file, final String fullFileNameInBucketFs,
            final ReadOnlyBucket bucket) {
        return true;
    }
}
