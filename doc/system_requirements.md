# System Requirement Specification BucketFS Java

## Introduction

BucketFS Java (BFSJ) is a Java library that abstracts client access to Exasol's [BucketFS](https://docs.exasol.com/database_concepts/bucketfs/bucketfs.htm). Users of this library can directly list contents, upload to or download from buckets without having to know the intricate details of the protocol.

## About This Document

### Target Audience

The target audience are Java software developers. See section ["Stakeholders"](#stakeholders) for more details.

### Goal

The goal of BucketFS Java is to make programmatic access to BucketFS available while removing the necessity to know about the underlying mechanisms.

### Quality Goals

BFSJ main quality goals are in descending order of importance:

1. Compact client code

## Stakeholders

### Software Developers

Java Software Developers use BFSJ to programmatically access and manipulate the contents of buckets in BucketFS.

### Terms and Abbreviations

The following list gives you an overview of terms and abbreviations commonly used in BFSJ documents.

* Bucket: container for files inside of BucketFS. Buckets can have individual access restrictions.
* BucketFS: service provided by the Exasol database that allows keeping files distributed across all data nodes of an Exasol cluster.

## Features

Features are the highest level requirements in this document that describe the main functionality of BucketFS Java.

### BucketFS Access

`feat~bucketfs-access~1`

BucketFS Java provides access to the BucketFS service(s) of the Exasol database.

Needs: req

## Functional Requirements

This section lists functional requirements from the user's perspective. The requirements are grouped by feature where they belong to a single feature.

### BucketFS Access

#### Creating new Buckets

`req~creating-new-buckets~1`

BFSJ creates a new bucket in BucketFS with a given name.

Rationale:

Allows adding new buckets in case the existing buckets are not sufficient.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Bucket Content Listing

`req~bucket-content-listing~1`

BFSJ lists the contents of a bucket in BucketFS.

Rationale:

Allows checking what &mdash; if anything &mdash; is currently inside a bucket.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Recursive Bucket Content Listing

`req~bucket-content-listing-recursive~1`

BFSJ lists the contents of a bucket in BucketFS recursively.

Rationale:

Allows listing all files in a directory hierarchy.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading a File to BucketFS

`req~uploading-a-file-to-bucketfs~1`

BFJS uploads a file from a locally accessible filesystem to a bucket.

Rationale:

This allows uploading data or UDF scripts to buckets.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading Text to a File in BucketFS

`req~uploading-text-to-a-file-in-bucketfs~1`

BFSJ uploads text (aka. a "string") to a file inside a bucket.

Rationale:

Often small files in Buckets need to be created on the fly. Configuration files for example or keys. Uploading from text allows d

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading InputStream to a File in BucketFS

`req~uploading-input-stream-to-a-file-in-bucketfs~1`

BFSJ uploads the contents of an InputStream to a file inside a bucket.

Rationale:

Especially integration tests often access resource content via `getResourceAsStream()`. Adding a method for this makes the test code more compact.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Conditional Upload

`req-conditional-upload~1`

For large files BFSJ can determine if its necessary to upload a file or if that file is already present in the Bucket at the given target path.

Rationale:

Uploading large files can take long. That slows down test.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Deleting a File from BucketFS

`req~deleting-a-file-from-bucketfs~1`

BFSJ can delete a file from BucketFS.

Needs: dsn

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

#### Downloading a File from BucketFS

`req~downloading-a-file-from-bucketfs~1`

BFSJ downloads a file from a bucket to a locally accessible filesystem.

Rationale:

This allows downloading files like e.g. logs from buckets.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Downloading a File from BucketFS as String

`req~downloading-a-file-from-bucketfs-as-string~1`

BFSJ downloads a file from a bucket as a string.

Rationale:

This allows downloading files and directly use from code.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Waiting for Bucket Content Synchronization

`req~waiting-for-bucket-content-synchronization~1`

BFSJ allows users to wait for bucket contents to be synchronized on a single node after uploading a file.

Rationale:

Files uploaded to BucketFS are not immediately usable due to internal synchronization mechanisms. In case of automated scripts, immediately continuing without waiting until the files are properly synced can lead to race conditions.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### TLS Support

`req~tls-support~1`

BFSJ allows users accessing a bucket via a connection encrypted with TLS.

Rationale:

* Unencrypted connections are insecure.
* Exasol Docker DB versions 8.29.1 and later only support TLS encrypted connections.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

##### Custom TLS Certificates

`req~tls-support.custom-certificate~1`

BFSJ allows users to connect to a database that uses a certificate that is not included in the runtime's keystore.

Rationale:

* Exasol databases (e.g. Docker DB) use a self signed certificate by default.
* Ignoring the certificate completely is not acceptable for security reasons.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

##### UDF bucket path

`req~udf-bucket-path~1`

To avoid confusion and to reduce the chance of human error, the bucket API returns the correct path for a bucket from the UDFs perspective.

Rationale:

* BucketFS is the only bit of filesystem a UDF can see, where users can store files.
* BucketFS in a UDF is a chroot environment, meaning that the paths in the UDF look different from the ones on the host or the ones exposed via the BucketFS web interface.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn
