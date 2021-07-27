# Introduction

## Terms and Abbreviations

<dl>
    <dt>BFSJ</dt><dd>BucketFS Java</dd>
</dl>

# Constraints

## Log-based Synchronization Check has a Minimum Resolution of one Second
`const~log-based-synchronization-check-has-a-minimum-resolution-of-one-second~1`

At least the monitoring solution based on cluster logs is limited to a resolution of one second.

That means this monitor cannot distinguish between subsequent uploads to the same object in a bucket if they are not at least one second apart.

For more details on what this means and how we deal with this constraint see the design decision in section ["How do we validate that objects on BucketFs are ready to use"](#how-do-we-validate-that-objects-on-bucketfs-are-ready-to-use).

Needs: dsn

# Solution Strategy

BucketFS offers a web API that is not compatible with established standards like [Web DAV](http://www.webdav.org/). While in some parts similar, the differences are big enough that standard client libraries can't be used. This library abstracts the underlying HTTP requests and responses to a level that lets users instead deal with buckets and their contents directly.

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `Bucket*Configuration`

The `Bucket*Configuration` is a set of objects representing the setup of an Exasol cluster.

Needs: impl

## `Bucket`

The `Bucket` building block controls interaction with a bucket in BucketFS.

# Runtime

This section describes the runtime behavior of the software.

## BucketFS Access

### List of `Bucket`Contents
`dsn~bucket-lists-its-contents~1`

The `Bucket` lists its contents as a set of object names.

Covers:

* `req~bucket-content-listing~1`

Needs: impl, itest

### Uploading to `Bucket`
`dsn~uploading-to-bucket~1`

The `Bucket` offers uploading a file from a locally accessible filesystem to a bucket in BucketFS.

Covers:

* `req~uploading-a-file-to-bucketfs~1`

Needs: impl, itest

### Uploading Strings to `Bucket`
`dsn~uploading-strings-to-bucket~1`

The `Bucket` offers uploading strings into a file in bucket in BucketFS.

Covers:

* `req~uploading-text-to-a-file-in-bucketfs~1`

Needs: impl, itest

### Uploading InputStream to `Bucket`
`dsn~uploading-input-stream-to-bucket~1`

The `Bucket` offers uploading of the contents of an `InputStream` into a file in that bucket on BucketFS.

Covers:

* `req~uploading-input-stream-to-a-file-in-bucketfs~1`

Needs: impl, itest

### Waiting Until File Appears in Target Directory
`dsn~waiting-until-file-appears-in-target-directory~1`

When uploading a file into a bucket, users can choose to block the call until the file appears in the bucket's target directory.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`

Needs: impl, itest

### Waiting Until Archive Extracted
`dsn~waiting-until-archive-extracted~1`

When uploading an archive of type `.tar.gz` or `.zip` into a bucket, users can choose to block the call until the archive is fully extracted in the bucket's target directory.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`

Needs: impl, itest

### Delete a file from a `Bucket`

`dsn~delete-a-file-from-a-bucket~1`

The `Bucket` offers deleting a file from a bucket.

Covers:

* `req~deleting-a-file-from-bucketfs~1`

Needs: impl, itest

### Downloading a file from a `Bucket`

`dsn~downloading-a-file-from-a-bucket~1`

The `Bucket` offers downloading a file from a bucket in BucketFS to a locally accessible filesystem.

Covers:

* `req~downloading-a-file-from-bucketfs~1`

Needs: impl, itest

### Downloading a file from a `Bucket` as string

`dsn~downloading-a-file-from-a-bucket-as-string~1`

The `Bucket` offers downloading a file from a bucket in BucketFS to as a Java string.

Covers:

* `req~downloading-a-file-from-bucketfs-as-string~1`

Needs: impl, itest

# Cross-cutting Concerns

# Design Decisions

## How do we Validate That Objects on BucketFS are Ready to Use?

BucketFS is a distributed filesystem with an HTTP interface. When users upload objects to a Bucket, it takes a while until they are really usable.

This is caused by various asynchronous processes an object has to go through, like node synchronization and extraction of archives.

In automated workflows, this is important, because reliable tests require objects to be available completely after they are uploaded.

### Alternatives considered

1. Checking via HTTP `GET`. Unfortunately this variant is not reliable.

1. Checking all nodes via HTTP `GET`. Suffers from the same problem as the previous idea and additionally requires that the client library knows all data nodes. On top of that, the variant's overhead grows proportionally with the number of nodes.

### Decisions

We decided to define a monitoring interface that a software that uses the library needs to implement. This allows at least consumers with access to cluster internal information to provide an implementation of this interface.

Users have the option to instantiate Bucket objects with synchronization checking if they provide a monitoring implementation. Otherwise they need to fall back to non-blocking operation.

#### Validating BucketFS Object Synchronization via the Monitoring API
`dsn~validating-bucketfs-object-synchronization-via-monitoring-api~1`

The `SyncAwareBucket` uses a `BucketFsMonitor` to check object synchronization.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`

Needs: impl, itest

#### BucketFS Object Overwrite Throttle
`dsn~bucketfs-object-overwrite-throttle~1`

The `SyncAwareBucket` delays subsequent uploads to the same path in a bucket so that the upload speed does not exceed the monitoring resolution of one second.

Comment:

The logs have a timestamp resolution of a second. That is why we delay a subsequent upload to the same path so that it starts after the next second.

Covers:

* `req~waiting-for-bucket-content-synchronization~1`
* `const~log-based-synchronization-check-has-a-minimum-resolution-of-one-second~1`

Needs: impl, itest

# Quality Scenarios

# Risks

# Acknowledgments

This document's section structure is derived from the "[arc42](https://arc42.org/)" architectural template by Dr. Gernot Starke, Dr. Peter Hruschka.
