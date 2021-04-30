# bucketfs-java

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

For more details see the [System Requirement Specification](doc/system_requirements.md).

## Table of Contents

### Information for Users

"Users" from the perspective of this project are software developers integrating BucketFS into their applications, not database end users.

* [User Guide](doc/user_guide/user_guide.md)
* [Changelog](doc/changes/changelog.md)

### Information for Contributors

Requirement, design documents and coverage tags are written in [OpenFastTrace](https://github.com/itsallcode/openfasttrace) format.

* [System Requirement Specification](doc/system_requirements.md)
* [Design](doc/design.md)
* [Dependencies](dependencies.md)