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

For more details on what this means and how we deal with this constraint see the design decision in section ["How do we validate that objects on BucketFS are ready to use"](#how-do-we-validate-that-objects-on-bucketfs-are-ready-to-use).

Needs: dsn

## Format of Entries in a `Bucket`

A `Bucket` can hold entries with common prefix and a slash `/` as separator. When interpreting this as a hierarchy similar to a file system, we need to consider that `Bucket` also allows having a file with the same name as a directory. For example `name/child.txt` and `name` can exist at the same time.

# Solution Strategy

BucketFS offers a web API that is not compatible with established standards like [Web DAV](http://www.webdav.org/). While in some parts similar, the differences are big enough that standard client libraries can't be used. This library abstracts the underlying HTTP requests and responses to a level that lets users instead deal with buckets and their contents directly.

## Requirement Overview

Please refer to the [System Requirement Specification](system_requirements.md) for user-level requirements.

# Building Blocks

This section introduces the building blocks of the software. Together those building blocks make up the big picture of the software structure.

## `Bucket*Configuration`

The `Bucket*Configuration` is a set of objects representing the setup of an Exasol cluster.

Needs: impl

## `CommandFactory`

The `CommandFactory` building block allows executing RPC commands like creating new buckets.

## `Bucket`

The `Bucket` building block controls interaction with a bucket in BucketFS.

# Runtime

This section describes the runtime behavior of the software.

## BucketFS Access

### Creating a new `Bucket` using the `CommandFactory`

The `CommandFactory` allows creating new buckets, specifying required arguments.

`dsn~creating-new-bucket~1`

Covers:

* `req~creating-new-buckets~1`

Needs: impl, itest

### List of `Bucket` Contents
`dsn~bucket-lists-its-contents~2`

The `Bucket` lists its contents as a sorted list of object names.

Covers:
* `req~bucket-content-listing~1`

Needs: impl, itest

### List of `Bucket` Contents With Common Prefix
`dsn~bucket-lists-files-with-common-prefix~1`

The list of contents of a path in the bucket contains files as well as sub-directories with this path as prefix.

Covers:
* `req~bucket-content-listing~1`

Needs: impl, utest

### List Files and Folders With Identical Name
`dsn~bucket-lists-file-and-directory-with-identical-name~1`

If `Bucket` contains two entries sharing the same prefix and only one of these entries has a path separator after the prefix, then list of contents of the bucket contains two entries.

Covers:
* `req~bucket-content-listing~1`

Needs: impl, utest

### Append Suffix to Directories
`dsn~bucket-lists-directories-with-suffix~1`

Directories in the list of bucket contents end with a slash `/` .

Rationale:
* This makes it easier for users to distinguish files from directories in a bucket listing. Especially if they have the same name.

Covers:
* `req~bucket-content-listing~1`

Needs: impl, utest

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

### Conditional Upload by Existence

`dsn~conditional-upload-by-existence~1`

BFSJ can check if a file needs to get uploaded by checking if the file exists in the Bucket.

Covers:

* `req-conditional-upload~1`

Needs: impl, itest

### Conditional Upload by Size

`dsn~conditional-upload-by-size~1`

BFSJ always uploads files less or equal than 1 MB.

Rationale:

For other files the checksum comparison would be too expensive.

Covers:

* `req-conditional-upload~1`

Needs: impl, itest

### Conditional Upload by Checksum

`dsn~conditional-upload-by-checksum~1`

BFSJ checks if a file needs to get uploaded by comparing the checksum.

Covers:

* `req-conditional-upload~1`

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

## How to Interpret Entries in `Bucket`?

See the constraint [format of entries in a `Bucket`](#format-of-entries-in-a-bucket).

### Alternatives Considered

The list of contents of a `Bucket` could either be represented as a hierarchy or as a flat list potentially with
* multiply entries sharing a common prefix
* prefix containing one or multiple slash `/` separators

### Decisions

#### Hierarchies of Entries

The design decides to interpret `Bucket` to contain a hierarchy of entries. Each entry may either be a *file* or a *directory*. An entry is a *directory* if it has children, otherwise the entry is a *file*. An entry has children when its name contains the BucketFS separator `/`.

Examples:
* `a.txt` is a *file*
* `a/b.txt` is interpreted as *directory* `a` containing file `b.txt`

This in particular affects the list of `Bucket` contents.

Rationale:
* A hierarchical representation of files and directories provides additional benefits:
  * Hierarchies are a convenient and familiar concept to users.
  * Hierarchies enable operations on multiple entries in a common scope, e.g. list, copy, or delete.

To support the coexistence of files and directories with the same name, directories should be represented with a slash `/` as suffix. The list of contents of a directory may then contain the same entry twice:
* once as file (without suffix)
* a second time as directory (with suffix)

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
