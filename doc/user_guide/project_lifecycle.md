# Project Lifecycle

This is a free and open source project. Updates are publicly available and free of charge.

Feature, documentation, bugfix and security updates are always provided as latest release.

The latest version gets tested for the compatibility with at least

* Latest Exasol Innovation Release
* Latest Exasol LTS Release

at the time of the release of this project version.

## End of Life

This project uses [semantic versioning](https://semver.org/). Versions with the same major version are guaranteed to be backward-compatible to previous versions with that major version.

Minor version updates add features that do not break compatibility and do not change hardware or software environment requirements beyond reasonable update rules. Fix versions only resolve bugs and / or add security updates.

| Version line | First release | End of support |
|--------------|---------------|----------------|
| 5.x.y        | 2026-07-27    | 2031-07-26     |

This library is easy to update with your application, so five years of support is enough.

## Security Updates

Users need to check the [changelog](../changes/changelog.md) to stay informed about security updates. You need to install the provided security updates in a timely manner to keep your setup secure. This is also true for any dependencies of this software that do not come bundled. An example is the Java Runtime Environment.

Exasol provides security updates until the EoL listed above.

Please refer to our [security policy](../../SECURITY.md) for details on coordinated vulnerability disclosure.

### Retaining Updates

Exasol distributes updates via GitHub releases. Even if the project should be archived, the releases remain accessible for download. Exasol will keep each security update accessible for at least 10 years.