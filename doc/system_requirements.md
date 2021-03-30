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

Features are the highest level requirements in this document that describe the main functionality of ETC.

### BucketFS Access
`feat~bucketfs-access~1`

ETC provides access to the BucketFS service(s) of the Exasol database.

Needs: req

## Functional Requirements

This section lists functional requirements from the user's perspective. The requirements are grouped by feature where they belong to a single feature.

### BucketFS Access

#### Bucket Content Listing
`req~bucket-content-listing~1`

ETC lists the contents of a bucket in BucketFS.

Rationale:

This is useful in integration test in order to determine whether required files in a bucket exist.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading a File to BucketFS
`req~uploading-a-file-to-bucketfs~1`

ETC uploads a file from a locally accessible filesystem to a bucket.

Rationale:

This allows uploading data or UDF scripts to buckets.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading Text to a File in BucketFS
`req~uploading-text-to-a-file-in-bucketfs~1`

ETC uploads text (aka. a "string") to a file inside a bucket.

Rationale:

Some tests require dynamically generated configuration data in files inside buckets. This requirement allows creating the configuration in the bucket without having to create a local file first.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Uploading InputStream to a File in BucketFS
`req~uploading-input-stream-to-a-file-in-bucketfs~1`

ETC uploads the contents of an InputStream to a file inside a bucket.

Rationale:

Some tests load the content from resources using `getResourceAsStream()`. 

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Downloading a File from BucketFS
`req~downloading-a-file-from-bucketfs~1`

ETC downloads a file from a bucket to a locally accessible filesystem.

Rationale:

This allows downloading files like e.g. logs from buckets.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn

#### Bucket Authentication
`req~bucket-authentication~1`

ETC handles the authentication against the buckets automatically.

Rationale:

Unless a test is about a 3rd-party software that accesses BucketFS from the outside, providing bucket credentials is unnecessary overhead in integration tests.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)


Needs: dsn

#### Waiting for Bucket Content Synchronization
`req~waiting-for-bucket-content-synchronization~1`

ETC allows integrators to wait for bucket contents to be synchronized on a single node after uploading a file.

Rationale:

Files uploaded to BucketFS are not immediately usable due to internal synchronization mechanisms. For integration tests it is necessary that integrators can rely on the file to be available in a bucket before running tests that depend on it.

Covers:

* [feat~bucketfs-access~1](#bucketfs-access)

Needs: dsn
