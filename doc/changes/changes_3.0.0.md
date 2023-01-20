# BucketFS Java 3.0.0, released 2023-01-20

Code name: `BucketFsServiceConfigurationProvider` and `ReadEnabledBucket.Builder.host()`

## Summary

Breaking changes were introduced back in version 2.6.0 without updating the major version. With the release of 3.0.0 the old **2.6.0 is deprecated**.

Those are the breaking changes compared to the 2.x.x line:

| 2.x.x                                                                      | 3.0.0                                  |
|----------------------------------------------------------------------------|----------------------------------------|
| `BucketFsSerivceConfigurationProvider`                                     | `BucketFsServiceConfigurationProvider` |
| `ReadEnabledBucket.Builder.ipAddress()` | `ReadEnabledBucket.Builder.host()` |

We also updated test dependencies and plugins. Functionally 3.0.0 is identical to the deprecated 2.6.0.

## Refactoring

* 3.0.0: Documented breaking changes

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-testcontainers:6.4.0` to `6.5.0`
* Updated `org.junit.jupiter:junit-jupiter-engine:5.9.1` to `5.9.2`
* Updated `org.junit.jupiter:junit-jupiter-params:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.10.0` to `5.0.0`

### Plugin Dependency Updates

* Updated `org.itsallcode:openfasttrace-maven-plugin:1.5.0` to `1.6.1`
