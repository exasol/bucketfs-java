# bucketfs-java 1.0.0, released 2021-03-31

## Code name: Migration from exasol-testcontainers

In version 1.0.0 we migrated the existing Bucket FS module from [`exasol-testcontainers`](https://github.com/exasol/exasol-testcontainers/) (ETC).

This allows developers to use the BucketFS library in their code without the additional dependencies that the ETC brings.

## Refactoring

* #1: Migrated code from `exasol-testcontainers`
* #3: Extracted and adapted system requirements and design

## Dependency Updates

### Test Dependency Updates

* Added `com.exasol:exasol-testcontainers:3.5.1`
* Added `org.hamcrest:hamcrest:2.2`
* Added `org.junit.jupiter:junit-jupiter-engine:5.7.1`
* Added `org.junit.jupiter:junit-jupiter-params:5.7.1`
* Added `org.mockito:mockito-junit-jupiter:3.8.0`
* Added `org.testcontainers:junit-jupiter:1.15.1`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:0.1.1`
* Added `com.exasol:project-keeper-maven-plugin:0.6.0`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.13`
* Added `org.apache.maven.plugins:maven-clean-plugin:2.5`
* Added `org.apache.maven.plugins:maven-compiler-plugin:3.8.1`
* Added `org.apache.maven.plugins:maven-deploy-plugin:2.7`
* Added `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M3`
* Added `org.apache.maven.plugins:maven-gpg-plugin:1.6`
* Added `org.apache.maven.plugins:maven-install-plugin:2.4`
* Added `org.apache.maven.plugins:maven-jar-plugin:2.4`
* Added `org.apache.maven.plugins:maven-javadoc-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-resources-plugin:2.6`
* Added `org.apache.maven.plugins:maven-site-plugin:3.3`
* Added `org.apache.maven.plugins:maven-source-plugin:3.2.1`
* Added `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3`
* Added `org.codehaus.mojo:versions-maven-plugin:2.7`
* Added `org.itsallcode:openfasttrace-maven-plugin:1.0.0`
* Added `org.jacoco:jacoco-maven-plugin:0.8.6`
* Added `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0`
* Added `org.sonatype.plugins:nexus-staging-maven-plugin:1.6.8`
