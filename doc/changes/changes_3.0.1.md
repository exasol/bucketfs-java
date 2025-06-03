# BucketFS Java 3.0.1, released 2025-06-??

Code name: Dependency updates on top of 3.0.0

## Summary

This release is a security update. We updated the dependencies of the project to fix transitive security issues.

We also added an exception for the OSSIndex for CVE-2024-55551, which is a false positive in Exasol's JDBC driver.
This issue has been fixed quite a while back now, but the OSSIndex unfortunately does not contain the fix version of 24.2.1 (2024-12-10) set.

## Features

* #76: Updated dependencies on top of 3.0.0

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `jakarta.json.bind:jakarta.json.bind-api:3.0.0` to `3.0.1`
* Updated `jakarta.json:jakarta.json-api:2.1.1` to `2.1.3`
* Updated `org.apache.commons:commons-compress:1.22` to `1.27.1`
* Removed `org.eclipse:yasson:3.0.2`

### Runtime Dependency Updates

* Added `org.eclipse:yasson:3.0.4`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.5.0` to `7.1.5`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Added `org.junit.jupiter:junit-jupiter-api:5.13.0`
* Removed `org.junit.jupiter:junit-jupiter-engine:5.9.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.2` to `5.13.0`
* Updated `org.mockito:mockito-junit-jupiter:5.0.0` to `5.18.0`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.21.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.1` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.1` to `5.1.0`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:2.5` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M6` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.0.1` to `3.2.7`
* Updated `org.apache.maven.plugins:maven-install-plugin:2.4` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.1` to `3.11.2`
* Updated `org.apache.maven.plugins:maven-resources-plugin:2.6` to `3.3.1`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.3` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M7` to `3.5.3`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:2.0.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.7.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.13.0` to `2.18.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:1.6.1` to `2.3.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `5.1.0.4751`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.13` to `1.7.0`
