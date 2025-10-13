# BucketFS Java 4.0.0, released 2025-10-??

Code name: Exasol 8.29.1 and later compatibility

## Summary

In Exasol versions after 8.29.1, the behavior of the BuckteFS interface changed subtly, causing some integration tests in this project to fail. We updated the tests for compatibility with 8.34.0, which was the latest version available at this time.

We also fixed the way the password encoding works in the `CreateBucketCommand` class for current versions of Exasol 8, since that was changed on the server side. Use the method `useBase64EncodedPasswords(boolean)` in the builder to control whether (until Exasol 7) or not (Exasol 8 and later) passwords send via the create command are Base64 encoded on the client side.

## Breaking Changes

We also removed the method `ReadEnabledBucket.httpPort` which was deprecated in favor of `ReadEnabledBucket.port` in 2.2.0 (2021).

## Bugfix

* #80: Updated integration tests to be compatible with Exasol 8.34.0

## Dependency Updates

### Test Dependency Updates

* Updated `org.junit.jupiter:junit-jupiter-api:5.13.0` to `5.13.4`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.0` to `5.13.4`
* Updated `org.mockito:mockito-junit-jupiter:5.18.0` to `5.20.0`
* Updated `org.testcontainers:junit-jupiter:1.21.1` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.4` to `2.0.5`
* Updated `com.exasol:project-keeper-maven-plugin:5.2.3` to `5.4.2`
* Updated `com.exasol:quality-summarizer-maven-plugin:0.2.0` to `0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1` to `9.0.2`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.0` to `3.14.1`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.3` to `3.5.4`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.7` to `3.2.8`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.11.2` to `3.12.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.3` to `3.5.4`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.0` to `1.7.3`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.18.0` to `2.19.1`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751` to `5.2.0.4988`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.7.0` to `0.8.0`
