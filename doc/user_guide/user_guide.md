# BucketFS Java User Guide

Exasol's [BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/bucketfs.htm) is a distributed file system that automatically synchronizes files uploaded on one cluster node to all other nodes. It is intended for providing shared configuration, scripts and libraries used in [User Defined Functions](https://docs.exasol.com/database_concepts/udf_scripts.htm) (UDF) mainly.

This project provides a library that abstracts access to Exasol's [BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/bucketfs.htm). That allows using BucketFS features programmatically without having to deal with the underlying protocol.

## Notation Used in This Document

Syntax definitions in this document are written in [Augmented Backus-Naur Form (ABNF)](https://www.rfc-editor.org/rfc/rfc5234).

## Getting BucketFS Java Into Your Project

[![Maven Central](https://img.shields.io/maven-central/v/com.exasol/bucketfs-java)](https://search.maven.org/artifact/com.exasol/bucketfs-java)

BucketFS Java is built using [Apache Maven](https://maven.apache.org/), so integrating the release packages into your project is easy with Maven.

Please check out ["Introduction to the Dependency Mechanism"](http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html), if you want to learn about how maven handles dependencies and dependency scopes.

We assume here that you are familiar with the basics.

### BucketFS Java as Maven Dependency

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

A "bucket" in BucketFS terms is a storage container that can contain multiple objects. Buckets are hosted by BucketFS services. They allow access to a bucket via a network protocol.

To work with BucketFS you need at least one service that hosts one bucket.

An object is roughly comparable to a file. In fact on cluster nodes BucketFS represents objects as files. Note though that this is an internal detail and could be subject to change at any time. An object has content and a path.

The full path consists of:

* name of IP or hostname of the machine on which the service runs
* name of the service hosting the bucket (aka. "BucketFS name")
* name of the bucket itself
* relative path to the object inside the bucket

## Object Synchronization

As mentioned before, BucketFS is a distributed file system. Due to that fact, it takes a while until objects uploaded to a bucket on one cluster node are distributed to all other cluster nodes.

Note that to safely use the objects, you need to wait until that synchronization is done.

Additionally BucketFS automatically expands a limited number of archive formats. While this is a very convenient feature, it again takes some time. The expanded contents can only be safely used after extraction from the archive finished.

BucketFS Java can help in both situation, provided that a synchronization monitor is available. Check section [Blocking vs. Non-Blocking Upload](#blocking-vs-non-blocking-upload) for details.

## The Bucket Interface

There are three interfaces with different kind of feature support for accessing buckets. The most limited only is the `ReadOnlyBucket` which only allows listing contents of and downloading objects from buckets.

The `UnsynchronizedBucket` interface adds upload capabilities. Albeit with no guarantee that objects are synchronized when the upload call comes back and also no means of telling this afterwards.

The main interface for interacting with BucketFS in this library is `Bucket`. It contains all methods mentioned before plus upload variants that allow blocking access. When you use those methods, the library guarantees that the objects are properly synchronized when the call returns.

If you prefer asynchronous operation, you can use the non-blocking variants on combination with a separate sync check that `Bucket` provides.

## Bucket Synchronization Monitor

Without help, the BucketFS library is not able to tell whether or not synchronization is done. That is why it defines the interface `BucketFsMonitor`. Code using the library that needs synchronization must implement this interface and inject it when creating a `Bucket`.

## Creating Bucket Objects

This example demonstrates how to create a `ReadOnlyBucket`.

```java
import com.exasol.bucketfs.ReadOnlyBucket;
import com.exasol.bucketfs.ReadEnabledBucket;

// ...
final ReadOnlyBucket bucket = ReadEnabledBucket.builder()
        .useTls(useTls)
        .raiseTlsErrors(raiseTlsErrors)
        .certificate(certificate)
        .host(host)
        .port(port)
        .serviceName(serviceName)
        .name(bucketName)
        .readPassword(readPassword)
        .build();
```

As mentioned before, `ReadOnlyBucket` is an interface. `ReadEnabledBucket` is an implementation of this interface.

Each bucket implementation comes with a builder that you create by calling the static method calling `builder()` on the implementation class.

The builder for the `ReadEnabledBucket` has the following parameter setters:

* Configure TLS behavior. See [below](#configuring-tls) for details.
    * `useTls`: `true` to use HTTPS, `false` to use HTTP (default). 
    * `raiseTlsErrors`: `true` to throw exceptions for errors verifying the TLS certificate of the server (default), `false` to ignore certificate errors (useful when using self-signed certificates)
    * `certificate`: `X509Certificate` to use when connecting via TLS
    * `allowAlternativeHostName`: if the certificate does not contain the correct host name, you can allow additional host names for connecting, e.g. `localhost`.
    * `allowAlternativeIpAddress`: if the certificate does not contain the correct host name, you can allow additional host names for connecting, e.g. `127.0.0.1`.
* `host`: Host name or IP address of the cluster node to which you want to connect
* `port`: number of the port the BucketFS service listens on
* `serviceName`: name of the service that hosts the bucket
* `name`: name of the bucket
* `readPassword`: in case the bucket is not public, this is the password that users need to supply in order to gain access (optional)

If you need to write to a bucket, the analogous builder call looks like this:

```java
final UnsynchronizedBucket bucket = WriteEnabledBucket.builder()
        .useTls(useTls)
        .raiseTlsErrors(raiseTlsErrors)
        .certificate(certificate)
        .allowAlternativeHostName(host())
        .host(host())
        .port(port)
        .serviceName(serviceName)
        .name(bucketName)
        .readPassword(readPassword)
        .writePassword(writePassword)
        .build();
```

Compared to creating the read-only bucket we have an additional setter here:

* `writePassword`: password required to write to the bucket

As mentioned before, if you need a bucket that supports blocking calls, you need to inject a sync monitor.

```java
final Bucket bucket = SyncAwareBucket.builder()
        .useTls(useTls)
        .raiseTlsErrors(raiseTlsErrors)
        .certificate(certificate)
        .host(host())
        .port(port)
        .serviceName(serviceName)
        .name(bucketName)
        .readPassword(readPassword)
        .writePassword(writePassword)
        .monitor(bucketFsMonitor)
        .build();
```

Which brings us to the last remaining builder setter:

* `monitor`: monitor that allows checking object synchronization (an implementation of the `BucketFsMonitor` interface)

## Working with Buckets

The Exasol test container provides access to buckets in BucketFS. This is useful if your tests need to work with files in buckets. If you for example want to test a UDF script, you can upload it prior to the test using a `Bucket` control object.

### Understanding Bucket Contents

One thing you need to know about buckets objects inside a bucket is that they are not stored in a hierarchical directory structure. Instead they are flat files that can have slashes or colons in their names so that it looks like a directory structure at first glance.

Since this internal detail deviates from what users are used to see, the bucket access methods implemented in this project simulate a regular structure.

Another thing worth noting is that if you store files in some selected archive formats (e.g. [TAR archives](https://www.gnu.org/software/tar/)) in a bucket, BucketFS presents the archive contents as a hierarchical structure.

### Specifying "Paths" Inside a Bucket

Some bucket actions require a path inside the bucket as parameter. While those paths are always relative to the root of the bucket, BucketFS Java lets you treat them as absolute paths too.

That means that the following two paths are both relative to the bucket root:

```
EXAClusterOS/
/EXAClusterOS/
```

### Listing Bucket Contents

The following code lists the contents of a bucket's root:

```java
final List<String> bucketContents = bucket.listContents();
```

You can also list the contents of a "path" within a bucket. "Path" is set in quotes here since objects in buckets are &mdash; as mentioned earlier &mdash; all files directly in the root of the bucket.

### Uploading a File to BucketFS

Especially when testing UDF scripts, this comes in handy. You can upload files from a local filesystem into a bucket as follows:

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

### Uploading Large File Only if Necessary

Uploading large files can be slow. To avoid that slowing down your tests, BFSJ can check if the file already exists in the same location on BucketFS and compare checksums. It will then only upload the file if the checksums differ. Since comparing the checksums also takes some time BFSJ only compares checksums for files larger than 1 MB. If a file is smaller, BFSJ uploads it regardless of whether it already existed.

To enable this feature use:

```java
bucket.setUploadNecessityCheckStrategy(new UploadNecessityCheckStrategy(sqlConnection));
```

To disable it again use:

```java
bucket.setUploadNecessityCheckStrategy(new UploadAlwaysStrategy());
```

By default, this feature is disabled.

### Uploading Text as a File

It's a common use-case test scenarios to create small files of well-defined content and upload them to BucketFS. Most of the time those are configuration files.

Use the following convenience method to write a string directly to a file in a bucket:

```java
bucket.uploadStringContent(content, destination); 
```

Here `content` is the `String` that you want to write an destination is again the path inside the bucket.

### Blocking vs. Non-blocking Upload

In integration tests you usually want reproducible test cases. This is why the standard implementation of `uploadFile(...)` blocks the call until the underlying object is synchronized in the bucket.

In rare cases you might want more control over that process, for example if you plan bulk-upload of a large number of small files and want to shift the check to the end of that operation.

For those special occasions there is the `uploadFileNonBlocking(source, destination)` method where you can choose to upload in non-blocking fashion.

The same style of overloaded function exists for text content upload too in the `uploadStringContentNonBlocking(content, destination)` method.

Unless you really need it and know exactly what you are doing, we recommend to stick to blocking operation for your tests.

### Delete a File from BucketFS

Deleting a file is straight forward:

```java
bucket.deleteFileNonBlocking(fileName);
```

Warning: If you try to upload a file shortly after you deleted it (e.g. less than 30 seconds later), the upload will fail with access denied. This is due to the implementation details of BucketFS.

### Downloading a File from BucketFS

Downloading a file is straight forward:

```java
bucket.downloadFile(source, destination);
```

Here the source is a path inside the bucket and destination is a path on a local file system.

### Managing Buckets and Services

Creating and deleting buckets and BucketFS services is not yet supported by the BFSJ.

## Working with the RPC API

The RPC API allows you to manage buckets themselves, i.e. create new buckets.

In order to work with the RPC API you first create a `CommandFactory`. This will allow you to execute commands, like creating a new bucket.

### Creating a `CommandFactory`

We recommend using the [exasol-testcontainers](https://github.com/exasol/exasol-testcontainers) because it simplifies getting the RPC URL and credentials.

First create a new test container `CONTAINER` as described [in the exasol-testcontainer user guide](https://github.com/exasol/exasol-testcontainers/blob/main/doc/user_guide/user_guide.md#creating-an-exasol-testcontainer-in-a-junit-5-test). Then you can create a new `CommandFactory` like this:

```java
final CommandFactory commandFactory = CommandFactory.builder()
        .raiseTlsErrors(true)
        .certificate(certificate)
        .serverUrl(CONTAINER.getRpcUrl())
        .bearerTokenAuthentication(CONTAINER.getClusterConfiguration().getAuthenticationToken())
        .build();
```

The builder for `CommandFactory` has the following parameter setters:

* Configure TLS behavior. See [below](#configuring-tls) for details.
    * `raiseTlsErrors`: `true` to throw exceptions for errors verifying the TLS certificate of the server (default), `false` to ignore certificate errors (useful when using self-signed certificates)
    * `certificate`: `X509Certificate` to use when connecting via TLS
* `serverUrl`: Configure the server URL, e.g. `https://<hostname>:443/jrpc` or `CONTAINER.getRpcUrl()` when using [exasol-testcontainers](https://github.com/exasol/exasol-testcontainers/).
* Configure authentication:
    * `bearerTokenAuthentication`: use bearer token authentication. When using [exasol-testcontainers](https://github.com/exasol/exasol-testcontainers/) you can retrieve the token via `CONTAINER.getClusterConfiguration().getAuthenticationToken()`.
    * `basicAuthentication`: use basic authentication with username and password, e.g. `basicAuthentication("username", "password")`.

### Creating a new bucket

First [create a new `CommandFactory`](#creating-a-commandfactory). Then you can use it to create a bucket:

```java
final String uniqueBucketName = "bucket_" + System.currentTimeMillis();

CreateBucketCommandBuilder commandBuilder = commandFactory.makeCreateBucketCommand()
        .bucketFsName("bfsdefault")
        .bucketName(uniqueBucketName)
        .isPublic(true)
        .readPassword("readPassword")
        .writePassword("writePassword");
        
commandBuilder.execute();
```

After executing the command, you can create a new `WriteEnabledBucket` or `SyncAwareBucket` as described above.

**Note:** It may take some time until the bucket is available.

#### Configuring TLS

There are two methods in the `CommandFactory.Builder` and the `ReadEnabledBucket.Builder` that control how TLS certificates are verified: `raiseTlsErrors()` and `certificate()`. You have these three options depending on your setup:

* With `raiseTlsErrors(true)` the HTTP client will throw an exception for any TLS errors (e.g. unknown certificate or invalid hostname). Use this when the database has an CA-signed certificate. This is the default.
* With `raiseTlsErrors(true).certificate(<certificate>)` you can specify a custom `X509Certificate`. This is useful when the database has a self-signed certificate with correct hostname.
* With `raiseTlsErrors(false)` the HTTP client will ignore any TLS errors. Use this when the database has a self-signed certificate with an invalid hostname. This is usually the case with the Exasol docker container that generates a self-signed certificate that is not valid when connecting to hostname `localhost`.
