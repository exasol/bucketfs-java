# bucketfs-java

<img alt="exasol-testcontainer logo" src="doc/images/bucketfs_java_128x128.png" style="float:left; padding:0px 10px 10px 10px;"/>

[![Build Status](https://api.travis-ci.com/exasol/bucketfs-java.svg?branch=master)](https://travis-ci.org/exasol/bucketfs-java)
[![Maven Central](https://img.shields.io/maven-central/v/com.exasol/bucketfs-java)](https://search.maven.org/artifact/com.exasol/bucketfs-java)

SonarCloud results:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Abucketfs-java&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Abucketfs-java)

This project provides a library that abstracts access to Exasol's [BucketFS](https://docs.exasol.com/administration/on-premise/bucketfs/bucketfs.htm). That allows using BucketFS features programmatically without having to deal with the underlying protocol.

## Features

* List contents of a bucket
* Upload to a bucket
* Download from a bucket

## Table of Contents

### Information for Users

"Users" from the perspective of this project are software developers integrating BucketFS into their applications, not database end users.

* [User Guide](doc/user_guide/user_guide.md)
* [Changelog](doc/changes/changelog.md)

### Information for Contributors

Requirement, design documents and coverage tags are written in [OpenFastTrace](https://github.com/itsallcode/openfasttrace) format.

* [System Requirement Specification](doc/system_requirements.md)
* [Design](doc/design.md)

## Dependencies

### Run Time Dependencies

Running the BucketFS Java requires a Java Runtime version 11 or later.

### Test Dependencies

| Dependency                                                                          | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Java Hamcrest](http://hamcrest.org/JavaHamcrest/)                                  | Checking for conditions in code via matchers           | BSD License                   |
| [JUnit](https://junit.org/junit5)                                                   | Unit testing framework                                 | Eclipse Public License 1.0    |
| [Mockito](http://site.mockito.org/)                                                 | Mocking framework                                      | MIT License                   |
| [Testcontainers](https://www.testcontainers.org/)                                   | Docker Container control abstraction                   | MIT License                   |

### Build Dependencies

| Plug-in                                                                             | Purpose                                                | License                       |
|-------------------------------------------------------------------------------------|--------------------------------------------------------|-------------------------------|
| [Apache Maven](https://maven.apache.org/)                                           | Build tool                                             | Apache License 2.0            |
| [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)    | Setting required Java version                          | Apache License 2.0            |
| [Maven GPG Plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)              | Code signing                                           | Apache License 2.0            |
| [Maven Enforcer Plugin][maven-enforcer-plugin]                                      | Controlling environment constants                      | Apache License 2.0            |
| [Maven Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)      | Creating a Javadoc JAR                                 | Apache License 2.0            |
| [Maven Jacoco Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)          | Code coverage metering                                 | Eclipse Public License 2.0    |
| [Maven Source Plugin](https://maven.apache.org/plugins/maven-source-plugin/)        | Creating a source code JAR                             | Apache License 2.0            |
| [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)   | Unit testing                                           | Apache License 2.0            |
| [OpenFastTrace Maven Plugin][oft-maven-plugin]                                      | Requirement Tracing                                    | GPL v3                        |
| [Project Keeper Maven Plugin][project-keeper-maven-plugin]                          | Checking project structure                             | MIT License                   |
| [Sonatype OSS Index Maven Plugin][sonatype-oss-index-maven-plugin]                  | Checking Dependencies Vulnerability                    | ASL2                          |
| [Versions Maven Plugin][versions-maven-plugin]                                      | Checking if dependencies updates are available         | Apache License 2.0            |

[maven-enforcer-plugin]: http://maven.apache.org/enforcer/maven-enforcer-plugin/
[oft-maven-plugin]: https://github.com/itsallcode/openfasttrace-maven-plugin
[project-keeper-maven-plugin]: https://github.com/exasol/project-keeper-maven-plugin
[sonatype-oss-index-maven-plugin]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[versions-maven-plugin]: https://www.mojohaus.org/versions-maven-plugin/
