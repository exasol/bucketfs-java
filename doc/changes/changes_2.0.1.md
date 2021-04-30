# bucketfs-java 2.0.1, released 2021-04-30

Code name: Improved repeated upload reliability

## Summary

In certain situations especially on 6.2.x there was a race condition that made detection of successful reupload to the same path in a bucket unreliable.
This has now been fixed. This scenario is realistically only happening in integration tests, but there it is likely.

Replaced handwritten dependency list with auto-generated list and linked it in the README.

## Bugfixes

* #4: Fixed race condition on repeated fast upload to the same path in a bucket.

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:0.6.1` to `0.7.0`
