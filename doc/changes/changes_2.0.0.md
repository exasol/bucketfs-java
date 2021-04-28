# bucketfs-java 2.0.0, released 2021-04-XX

Code name:

Breaking changes:

* Removed obsolete method `upload<...>(<...>, boolean blocking)` from interface `Bucket`.
* Wrapped all `InterruptedExceptions` in `BucketAccessExceptions` (unchecked exception).
* File uploads now throw `FileNotFoundException` in case source file does not exist.

## Features

* #14: Added `downloadFileAsString` method.

## Bugfixes

* #18: Improved log output in case the optional service name is not present in a bucket object.
* #19: Better distinction between different error causes when uploading or downloading files.

## Dependency Updates

### Compile Dependency Updates

* Added `com.exasol:error-reporting-java:0.4.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:3.5.1` to `3.5.2`
* Updated `org.mockito:mockito-junit-jupiter:3.8.0` to `3.9.0`
* Updated `org.testcontainers:junit-jupiter:1.15.1` to `1.15.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:0.1.1` to `0.2.0`
* Updated `com.exasol:project-keeper-maven-plugin:0.6.0` to `0.6.1`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:2.7` to `3.0.0-M1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.8.1`
