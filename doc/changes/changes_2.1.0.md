# BucketFS Java 2.1.0, released 2021-07-30

Code name: Delete bucket object and conditional upload

## Summary:

In this release we added a method for deleting files from BucketFS.

Additionally users can no provide a strategy that decides whether an upload is really necessary or if it can be skipped. We also added a ready-to-use strategy that checks via hash sums if the file to be uploaded is already present. This can speed up tests significantly if they would otherwise repeatedly upload the same large files.

## Features:

* #30: Added method for deleting files
* #33: Added conditional upload

## Bug Fixes:

* #28: Fixed upload timeout for large files

## Dependency Updates

### Compile Dependency Updates

* Added `jakarta.json.bind:jakarta.json.bind-api:2.0.0`
* Added `org.apache.commons:commons-compress:1.21`
* Added `org.eclipse:yasson:2.0.2`
* Added `org.glassfish:jakarta.json:2.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.2` to `4.0.1`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.1` to `5.7.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.9.0` to `3.12.4`
* Updated `org.testcontainers:junit-jupiter:1.15.3` to `1.16.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.2.0` to `0.6.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.7.0` to `1.0.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:1.6` to `3.0.1`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0` to `3.3.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.6` to `0.8.7`
