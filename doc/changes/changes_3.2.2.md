# BucketFS Java 3.2.2, released 2025-06-05

Code name: Dependency updates on top of 3.2.1

## Summary

This release is a security update. We updated the dependencies of the project to fix transitive security issues.

We also added an exception for the OSSIndex for CVE-2024-55551, which is a false positive in Exasol's JDBC driver.
This issue has been fixed quite a while back now, but the OSSIndex unfortunately does not contain the fix version of 24.2.1 (2024-12-10) set.

## Features

* #76: Updated dependencies on top of 3.2.1

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.1` to `7.1.5`
* Added `org.junit.jupiter:junit-jupiter-api:5.13.0`
* Removed `org.junit.jupiter:junit-jupiter-engine:5.11.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.11.3` to `5.13.0`
* Updated `org.mockito:mockito-junit-jupiter:5.14.2` to `5.18.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.16` to `2.0.17`
* Updated `org.testcontainers:junit-jupiter:1.20.3` to `1.21.1`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.4.0` to `5.1.0`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.17`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.13.0` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.2` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.1` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.3` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.10.1` to `3.11.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.1` to `3.5.3`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.17.1` to `2.18.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.8.0` to `2.3.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121` to `5.1.0.4751`
