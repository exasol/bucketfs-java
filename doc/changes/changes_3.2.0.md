# BucketFS Java 3.2.0, released 2024-08-01

Code name: Specify additional Subject Alternative Names (SAN) for certificate

## Summary

This release supports establishing TLS connections to Exasol Docker DB version 8.29.1 and later by allowing users to specify additional Subject Alternative Names (SAN) for the TLS certificate. See the [user guide](../user_guide/user_guide.md#additional-host-names) for details.

## Features

* #69: Allowed specifying Subject Alternative Names (SAN) for certificate

## Dependency Updates

### Compile Dependency Updates

* Updated `jakarta.json.bind:jakarta.json.bind-api:3.0.0` to `3.0.1`

### Runtime Dependency Updates

* Updated `org.eclipse.parsson:parsson:1.1.5` to `1.1.7`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.0.1` to `7.1.0`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.10.2` to `5.10.3`
* Updated `org.junit.jupiter:junit-jupiter-params:5.10.2` to `5.10.3`
* Updated `org.mockito:mockito-junit-jupiter:5.10.0` to `5.12.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.13`
* Updated `org.testcontainers:junit-jupiter:1.19.6` to `1.20.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.0` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.1.0` to `4.3.3`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.12.1` to `3.13.0`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.1` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.1.0` to `3.2.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.6.3` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.12`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `4.0.0.4121`
* Updated `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.13` to `1.7.0`
