# BucketFS Java 3.1.0, released 2023-04-11

Code name: Removed duplicate classes from dependencies

## Summary

This release adds a method for recursively listing the content of a bucket. It also removes duplicate classes from dependencies and switched from Jakarta JSON implementation to [Eclipse Parsson](https://projects.eclipse.org/projects/ee4j.parsson) and [Yasson](https://projects.eclipse.org/projects/ee4j.yasson).

## Features

* #57: Supported recursive listing of bucket contents

## Bugfixes

* #59: Removed duplicate classes from dependencies

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Removed `org.apache.commons:commons-compress:1.22`
* Removed `org.eclipse:yasson:3.0.2`

### Runtime Dependency Updates

* Added `org.eclipse.parsson:parsson:1.1.1`
* Added `org.eclipse:yasson:3.0.3`
* Removed `org.glassfish:jakarta.json:2.0.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.5.0` to `6.5.1`
* Updated `org.mockito:mockito-junit-jupiter:5.0.0` to `5.2.0`
* Added `org.slf4j:slf4j-jdk14:2.0.7`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.18.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `2.9.6`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0` to `3.1.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.2.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M6` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M7` to `3.0.0-M8`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.14.2`
