# BucketFS Java 2.5.0, released 2022-12-09

Code name: List Files and Folders Hierarchically

## Summary

When BucketFS contains multiple files in a common folder the list returned by `ReadEnabledBucket.parseContentListResponseBody()` contained the common folder multiple times. This has been fixed by listing only distinct entries.

Additionally this release compensates for files and folders sharing the same name by appending a suffix to folders.

## Bugfixes

* #51: Ensured list of BucketFS contents to contain only unique entries.

## Dependency Updates

### Compile Dependency Updates

* Updated `org.apache.commons:commons-compress:1.21` to `1.22`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.3.0` to `6.4.0`
* Updated `org.mockito:mockito-junit-jupiter:4.8.1` to `4.9.0`
* Updated `org.testcontainers:junit-jupiter:1.17.5` to `1.17.6`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.2` to `1.2.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.8.0` to `2.9.1`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M7`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.13.0`
