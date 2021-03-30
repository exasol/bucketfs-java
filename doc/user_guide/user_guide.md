# BucketFS Java User Guide

Exasol's [BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/bucketfs.htm) is a distributed file system that automatically synchronizes files uploaded on one cluster node to all other nodes. It is intended for providing shared configuration, scripts and libraries used in [User Defined Functions](https://docs.exasol.com/database_concepts/udf_scripts.htm) (UDF) mainly.

This project provides an library that abstracts access to Exasol's [BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/bucketfs.htm). That allows using BucketFS features programmatically without having to deal with the underlying protocol.

## Notation Used in This Document

Syntax definitions in this document are written in [Augmented Backus-Naur Form (ABNF)](https://tools.ietf.org/html/rfc5234).

## Getting BucketFS Java Into Your Project

BucketFS Java is built using [Apache Maven](https://maven.apache.org/), so integrating the release packages into your project is easy with Maven.

Please check out ["Introduction to the Dependency Mechanism"](http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html), if you want to learn about how maven handles dependencies and dependency scopes.

We assume here that you are familiar with the basics.

### Exasol Test Containers as Maven Dependency

Just add the following dependency to add the Exasol test containers to your project.

```xml
<dependency>
    <groupId>com.exasol</groupId>
    <artifactId>bucketfs-java</artifactId>
    <version><!-- add latest version here --></version>
</dependency>
```

As always, check for the latest version of the dependencies.

## Services, Buckets, Objects and Paths

A "bucket" in BucketFS terms is a storage container that can contain multiple objects. Buckets are hosted by BucketFS services. They allowing access to a bucket via a network protocol.

To work with BucketFS you need at least one service that hosts one bucket.

An object is roughly comparable to a file. In fact on cluster nodes BucketFS represents objects as files. Note though that this is an internal detail and could be subject to change at any time.
An object has content and a path.

The full path consists of:

* name of IP or hostname of the machine on which the service runs
* name of the service hosting the bucket
* name of the bucket itself
* relative path to the object inside the bucket

## Object Synchronization

As mentioned before, BucketFS is a distributed file system. Due to that fact, it takes a while until objects uploaded to a bucket on one cluster node are distributed to all other cluster nodes.

Note that to safely use the objects, you need to wait until that synchronization is done.

Additionally BucketFS automatically expands a limited number of archive formats. While this is a very convenient feature, it again takes some time. The expanded contents can only be safely used after extraction from the archive finished.

## The Bucket Interface

There are three interfaces with different kind of feature support for accessing buckets. The most limited only is the `ReadOnlyBucket` which only allows listing contents of and downloading objects from buckets.

The `UnsynchronizedBucket` interface adds upload capabilities. Albeit with no guarantee that objects are synchronized when the upload call comes back and also no means of telling this afterwards.

The main interface for interacting with BucketFS in this library is `Bucket`. It contains all methods mentioned before plus upload variants that allow blocking access. When you use those methods, the library guarantees that the objects are properly synchronized when the call returns.

If you prefer asynchronous operation, you can use the non-blocking variants on combination with a separate sync check that `Bucket` provides.

## Creating Bucket Objects

This Example demonstrates how to create a `ReadOnlyBucket`.

```java
import com.exasol.bucketfs.ReadOnlyBucket;
import com.exasol.bucketfs.ReadEnabledBucket;

// ...
final ReadOnlyBucket = ReadEnabledBucket.builder()//
                .ipAddress(ipAddress) //
                .httpPort(port) //
                .serviceName(serviceName) //
                .name(bucketName) //
                .readPassword(readPassword) //
                .build();
```

As mentioned before, `ReadOnlyBucket` is an interface. `ReadEnabledBucket` is an implementation of this interface.

Each bucket implementation comes with a builder that you create by calling the static method calling `builder()` on the implementation class.

The builder for the `ReadEnalbedBucket` has the following parameter setters:

* `ipAddress`: IP address of the cluster node to which you want to connect
* `httpPort`: number of the port the BucketFS service listens on
* `serviceName`: name of the service that hosts the bucket
* `name`: name of the bucket
* `readPassword`: in case the bucket is not public, this is the password that users need to supply in order to gain access (optional)

## Working with Buckets

The Exasol test container provides access to buckets in BucketFS. This is useful if your tests need to work with files in buckets. If you for example want to test a UDF script, you can upload it prior to the test using a `Bucket` control object. 

### Understanding Bucket Contents

One thing you need to know about buckets objects inside a bucket is that they are not stored in a hierarchical directory structure. Instead they are flat files that can have slashes in their names so that it looks like a directory structure at first glance.

Since this internal detail deviates from what users are used to see, the bucket access methods implemented in this project simulate a regular structure.

Another thing worth noting is that if you store files in some selected archive formats (e.g. [TAR archives](https://www.gnu.org/software/tar/)) in a bucket, BucketFS presents the archive contents as a hierarchical structure.

### Specifying "Paths" Inside a Bucket

Some bucket actions require a path inside the bucket as parameter. While those paths are always relative to the root of the bucket, the Exasol test container lets you treat them as absolute paths too.

That means that the following two paths are both relative to the bucket root:

```
EXAClusterOS/
/EXAClusterOS/
```

### Getting a Bucket Control Object

You can get access to a bucket in BucketFS by requesting a `Bucket` control object from the container.

```java
final Bucket bucket = this.container.getBucket("mybucketfs", "mybucket");
```

If you just need access to the default bucket (i.e. the bucket which in a standard setup of Exasol always exists), use the following convenience method.

```java
final Bucket bucket = this.container.getDefaultBucket();
```

### Listing Bucket Contents

The following code lists the contents of a buckets root.

```java
final List<String> bucketContents = bucket.listContents();
```

You can also list the contents of a "path" within a bucket. "Path" is set in quotes here since objects in buckets are &mdash; as mentioned earlier &mdash; all files directly in the root of the bucket.

### Uploading a File to BucketFS

Especially when testing UDF scripts, this comes in handy. You can upload files from a local filesystem into a bucket as follows.

```java
bucket.uploadFile(source, destination);
```

Where `source` is an object of type `Path` that points to a local file system and `destination` is a string defining the path relative to the bucket's root to where the file should be uploaded.

### Uploading Files Into a "Directory"

As mentioned in section ["Specifying Paths Inside a Bucket"](#specifying-paths-inside-a-bucket) BucketFS only simulates a path structure. For your convenience the file upload lets you choose a "directory" in the bucket to which you want to upload.

If you chose this variant, the original filename from the local path is appended to the path inside the bucket.

As an example let's assume you want to upload a jar file from a local directory like this:

```java
bucket.uploadFile("repo/virtual-schemas/3.0.1/virtual-schemas-3.0.1.jar", "jars/");
```

In this case the `Bucket` treats the destination path in the bucket as if you wrote `jars/virtual-schemas-3.0.1.jar`.

### Uploading Text as a File

It's a common use-case test scenarios to create small files of well-defined content and upload them to BucketFS. Most of the time those are configuration files.

Use the following convenience method to write a string directly to a file in a bucket.

```java
bucket.uploadStringContent(content, destination); 
```

Here `content` is the `String` that you want to write an destination is again the path inside the bucket.

### Blocking vs. Non-blocking Upload

In integration tests you usually want reproducible test cases. This is why the standard implementation of `uploadFile(...)` blocks the call until the underlying object is synchronized in the bucket.

In rare cases you might want more control over that process, for example if you plan bulk-upload of a large number of small files and want to shift the check to the end of that operation.

For those special occasions there is an overloaded method `uploadFile(source, destination, blocking-flag)` where you can choose to upload in non-blocking fashion. 

The same style of overloaded function exists for text content upload too in the method `upload(content, destination, blocking-flag)`.

Unless you really need it and know exactly what you are doing, we recommend to stick to blocking operation for your tests.

### Downloading a File from BucketFS

Downloading a file is straight forward:

```java
bucket.downloadFile(source, destination);
```

Here the source is a path inside the bucket and destination is a path on a local file system.

### Managing Buckets and Services

Creating and deleting of buckets and BucketFS services is not yet supported by the Exasol test container.