# BucketFS Java 4.0.1, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-9563 in org.eclipse.parsson:parsson:jar:1.1.7:runtime

## Summary

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

* Updated `com.exasol:exasol-testcontainers:7.1.7` to `7.3.0`
* Updated `org.junit.jupiter:junit-jupiter-api:5.13.4` to `6.1.1`
* Updated `org.junit.jupiter:junit-jupiter-params:5.13.4` to `6.1.1`
* Updated `org.mockito:mockito-junit-jupiter:5.20.0` to `5.23.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.17` to `2.0.18`
* Updated `org.testcontainers:junit-jupiter:1.21.3` to `1.21.4`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.4.2` to `5.7.2`
