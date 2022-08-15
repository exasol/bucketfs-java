# BucketFS Java 2.4.0, released 2022-??-??

Code name: State based monitoring

## Summary

Enhanced BucketFS to allow filtering entries in BucketFS log file not only by time stamp but also by line number.

Updated interface to allow to pass a generic state which can represent the current point in time or the size of the log file in terms of number of lines before looking for new entries.

Please note that the interface changes should be backwards-compatible for _consumers_ of the interface, while existing implementations of the interface are broken in contrast.

Exasol product integration team assumes that the only existing implementations are inside projects [bucketfs-java](https://github.com/exasol/bucketfs-java) and [exasol-testcontainers](https://github.com/exasol/exasol-testcontainers).

Old:
```java
interface com.exasol.bucketfs.Bucket {
  boolean isObjectSynchronized(String pathInBucket, Instant afterUTC) throws BucketAccessException;
}
```
New:
```java
interface com.exasol.bucketfs.Bucket {
  boolean isObjectSynchronized(String pathInBucket, State state) throws BucketAccessException;
}
```

In order to use the new signature of `isObjectSynchronized()` please refer to the following classes:
* `com.exasol.bucketfs.monitor.TimestampState`
* `com.exasol.bucketfs.monitor.TimestampRetriever`

## Features

* #44: Supported filtering entries in BucketFS log file by line number

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.0` to `0.4.1`
* Updated `org.eclipse:yasson:2.0.2` to `2.0.4`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:5.1.0` to `6.1.2`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.7.2` to `5.8.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.7.2` to `5.8.2`
* Updated `org.mockito:mockito-junit-jupiter:3.12.4` to `4.5.1`
* Updated `org.testcontainers:junit-jupiter:1.16.0` to `1.17.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.6.0` to `1.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.4` to `2.6.2`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.15`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3` to `3.0.0-M6`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.3.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.8.1` to `2.10.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.7` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8` to `1.6.13`
