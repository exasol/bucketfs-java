package com.exasol.bucketfs;

public abstract class AbstractBucketBuilder<T extends AbstractBucketBuilder<T>> {
    protected abstract T self();
}