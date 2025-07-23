# BucketFS Java 4.0.0, released 2025-07-23

Code name: Exasol 8.29.1 and later compatibility

## Summary

In Exasol versions after 8.29.1, the behavior of the BuckteFS interface changed subtly, causing some integration tests in this project to fail. We updated the tests for compatibility with 8.34.0, which was the latest version available at this time.

## Breaking Changes

We also removed the method `ReadEnabledBucket.httpPort` which was deprecated in favor of `ReadEnabledBucket.port` in 2.2.0 (2021). 

## Bugfix

* #80: Updated integration tests to be compatible with Exasol 8.34.0
## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.5` to `7.1.7`
* Updated `org.junit.jupiter:junit-jupiter-api:5.13.0` to `5.13.4`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.0` to `5.13.4`
* Updated `org.testcontainers:junit-jupiter:1.21.1` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.1.0` to `5.2.2`
* Added `org.sonatype.central:central-publishing-maven-plugin:0.7.0`
* Removed `org.sonatype.plugins:nexus-staging-maven-plugin:1.7.0`
