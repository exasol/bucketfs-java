# BucketFS Java 2.4.1, released 2022-??-??

Code name: Replace redundant classes

## Summary

Due to dependencies and concurrent implementation temporarily some classes needed be created redundantly in the current repository as well as in exasol-testcontainers.

The current release replaces the redundant implementations by the default implementations from exasol-testcontainers.

## Refactorings

* #46: Replaced temporarily created redundant implementations by classes from exasol-testcontainers.

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Updated `jakarta.json.bind:jakarta.json.bind-api:2.0.0` to `3.0.0`
* Updated `jakarta.json:jakarta.json-api:2.0.1` to `2.1.1`
* Updated `org.eclipse:yasson:2.0.4` to `3.0.2`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.1.2` to `6.2.0`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.8.2` to `5.9.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.8.2` to `5.9.1`
* Updated `org.mockito:mockito-junit-jupiter:4.5.1` to `4.8.0`
* Updated `org.testcontainers:junit-jupiter:1.17.1` to `1.17.4`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:2.6.2` to `2.8.0`
