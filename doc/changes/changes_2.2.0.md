# BucketFS Java 2.2.0, released 2021-09-27

Code name: Create buckets and support TLS

## Summary

Release 2.2.0 adds support for creating new buckets in BucketFS. This is useful for integration tests that require a new, empty bucket. see the [user guide](https://github.com/exasol/bucketfs-java/blob/main/doc/user_guide/user_guide.md#creating-a-new-bucket) for details how to use the new API.

This version also adds support for using TLS for connecting to Buckets and the RPC API. See the [user guide](https://github.com/exasol/bucketfs-java/blob/main/doc/user_guide/user_guide.md#configuring-tls) for details.

## Features

* #17: Added support for creating new buckets
* #35: Added support using self-signed certificates and ignoring TLS errors
* #35: Increased upload timeout for `SyncAwareBucket` from 60s to 120s to fix sporadic test failures

## Dependency Updates

### Compile Dependency Updates

* Added `jakarta.json.bind:jakarta.json.bind-api:2.0.0`
* Added `org.eclipse:yasson:2.0.2`
* Added `org.glassfish:jakarta.json:2.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.3` to `5.1.0`
* Updated `org.mockito:mockito-junit-jupiter:3.11.2` to `3.12.4`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.5.0` to `0.6.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.10.0` to `1.2.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0` to `3.3.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.6` to `0.8.7`
