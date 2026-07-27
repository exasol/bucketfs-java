# BucketFS Java 4.0.1, released 2026-07-24

Code name: Fixed vulnerability CVE-2026-9563 in org.eclipse.parsson:parsson:jar:1.1.7:runtime

## Summary

**Breaking Change:** Starting with this release, Exasol version 7.1 is no longer supported. We only test against the latest version and the latest LTS version.

This release fixes the following vulnerability:

### CVE-2026-9563 (CWE-400) in dependency `org.eclipse.parsson:parsson:jar:1.1.7:runtime`
In Eclipse Parsson published Maven Central artifacts before version 1.1.8, the JSON parser did not enforce a default maximum on the number of characters consumed while parsing a single JSON document. Applications that parse attacker- controlled JSON can be forced to consume excessive CPU and memory by processing very large documents, including large arrays, objects, strings, numbers, whitespace, or nested structures, resulting in a denial of service. Eclipse Parsson 1.1.8 introduces a configurable maximum parsing limit with a default limit of 15 million parser-consumed characters.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-9563?component-type=maven&component-name=org.eclipse.parsson%2Fparsson&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-9563
* https://github.com/eclipse-ee4j/parsson/pull/169
* https://gitlab.eclipse.org/security/vulnerability-reports/-/work_items/444

## Security

* #85: Fixed vulnerability CVE-2026-9563 in dependency `org.eclipse.parsson:parsson:jar:1.1.7:runtime`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.1` to `1.0.2`
* Updated `jakarta.json.bind:jakarta.json.bind-api:3.0.1` to `3.0.2`

### Runtime Dependency Updates

* Updated `org.eclipse.parsson:parsson:1.1.7` to `1.1.9`

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:7.1.7` to `8.0.0`
* Removed `org.junit.jupiter:junit-jupiter-api:5.13.4`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.4` to `5.14.4`
* Updated `org.mockito:mockito-junit-jupiter:5.20.0` to `5.23.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.17` to `2.0.18`
* Removed `org.testcontainers:junit-jupiter:1.21.3`
* Added `org.testcontainers:testcontainers-junit-jupiter:2.0.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.5` to `2.1.0`
* Updated `com.exasol:project-keeper-maven-plugin:5.4.2` to `5.7.4`
* Removed `com.exasol:quality-summarizer-maven-plugin:0.2.1`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.2` to `10.0.0`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.0` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.14.1` to `3.15.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.6.1` to `3.6.3`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.4` to `3.5.6`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.3.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.21.0` to `3.22.0`
* Updated `org.apache.maven.plugins:maven-source-plugin:3.2.1` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.4` to `3.5.6`
* Added `org.codehaus.mojo:build-helper-maven-plugin:3.6.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.19.1` to `2.21.0`
* Updated `org.itsallcode:openfasttrace-maven-plugin:2.3.0` to `2.3.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.13` to `0.8.15`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.2.0.4988` to `5.7.0.6970`
* Updated `org.sonatype.central:central-publishing-maven-plugin:0.8.0` to `0.11.0`
* Added `org.spdx:spdx-maven-plugin:1.0.4`
