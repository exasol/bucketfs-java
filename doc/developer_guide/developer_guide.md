# Developer Guide

## Change Management

Changes are planned via GitHub issues. Except for very small changes, we require and issue ticket before accepting pull requests.

Changes must be put on a new branch that follows this naming convention:

    <type>/<issue-number>_<title>

Where `<type>` is one of `feature`, `bugfix`, `refactoring`, `documentation`, or `security`.

## API Documentation

The API is documented inline in the code using [JavaDoc](https://www.oracle.com/java/technologies/javase/javadoc.html) and rendered to HTML under `target/reports/apidocs/index.html`.

## Testing

This project uses JUnit 5 as a testing framework. Run tests and valiations with the following command.

```shell
mvn clean verify
```

The unit test results will be logged under `target/surefire-reports`.

Test coverage is measured using [JaCoCo](https://www.jacoco.org/jacoco/). 80% or more are required on all new code and the total project.

## Validation

Each pull request goes though the following unification and validation steps:

1. [Eclipse Formatter](../../.settings)
2. [Project Keeper](../../.project-keeper.yml)
3. [SonarQube Cloud](https://sonarcloud.io/project/overview?id=com.exasol%3Abucketfs-java)
4. [Link checker](../../.github/workflows/broken_links_checker.yml)
5. Sonatype Guide (fka. "OSSIndex")
6. Code and document review by code owners

The following re-validation steps run regularly.

1. Workflows to [identify](../../.github/workflows/dependencies_check.yml) and [address](../../.github/workflows/dependencies_update.yml) issues found by Sonatype Guide
2. Dependabot

Dependabot is globally activated and managed for all Exasol repositories.

## Releases

Releases are done via GHA in the [release workflow](../../.github/workflows/release.yml) and published  [in the project repository on GitHub](https://github.com/exasol/bucketfs-java/releases) and in the [Central Repository](https://repo1.maven.org/maven2/com/exasol/bucketfs-java/).

Before publishing, the release workflow verifies that CI succeeded for the release commit and verifies the release preconditions.

## Bill of Materials

This is a Maven Project controlled by the [`pom.xml`](pom.xml). Additionally, each GitHub [release](https://github.com/exasol/bucketfs-java/releases) is accompanied by an SPDX version 3 SBOM.
