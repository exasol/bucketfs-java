# BucketFS Java 2.1.0, released 2021-??-??

Code name: Added delete method

## Summary:

In this release we added a method for deleting files from BucketFs.

## Features:

* #30: Added method for deleting files

## Bug Fixes:

* #28: Fixed upload timeout for large files

## Dependency Updates

### Compile Dependency Updates

* Added `org.apache.commons:commons-compress:1.21`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.2` to `3.5.3`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.1` to `5.7.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.1` to `5.7.2`
* Updated `org.mockito:mockito-junit-jupiter:3.9.0` to `3.11.2`
* Updated `org.testcontainers:junit-jupiter:1.15.3` to `1.16.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.2.0` to `0.5.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.7.0` to `0.10.0`
