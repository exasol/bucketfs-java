<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT License][1]                                                                                             |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Jakarta JSON Binding API][5]    | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |

## Test Dependencies

| Dependency                                 | License                          |
| ------------------------------------------ | -------------------------------- |
| [JUnit Jupiter Params][6]                  | [Eclipse Public License v2.0][7] |
| [Hamcrest][8]                              | [BSD-3-Clause][9]                |
| [mockito-junit-jupiter][10]                | [MIT][11]                        |
| [Test containers for Exasol on Docker][12] | [MIT License][13]                |
| [SLF4J JDK14 Provider][14]                 | [MIT][15]                        |

## Runtime Dependencies

| Dependency            | License                                                                                                      |
| --------------------- | ------------------------------------------------------------------------------------------------------------ |
| [Eclipse Parsson][16] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Yasson][17]          | [Eclipse Public License v. 2.0][18]; [Eclipse Distribution License v. 1.0][19]                               |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][20]                       | [GNU LGPL 3][21]                               |
| [Apache Maven Toolchains Plugin][22]                    | [Apache-2.0][23]                               |
| [Apache Maven Compiler Plugin][24]                      | [Apache-2.0][23]                               |
| [Apache Maven Enforcer Plugin][25]                      | [Apache-2.0][23]                               |
| [Maven Flatten Plugin][26]                              | [Apache Software License][23]                  |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][27] | [ASL2][28]                                     |
| [Maven Surefire Plugin][29]                             | [Apache-2.0][23]                               |
| [Versions Maven Plugin][30]                             | [Apache License, Version 2.0][23]              |
| [duplicate-finder-maven-plugin Maven Mojo][31]          | [Apache License 2.0][32]                       |
| [Apache Maven Artifact Plugin][33]                      | [Apache-2.0][23]                               |
| [Apache Maven Deploy Plugin][34]                        | [Apache-2.0][23]                               |
| [Apache Maven Source Plugin][35]                        | [Apache-2.0][23]                               |
| [Apache Maven Javadoc Plugin][36]                       | [Apache-2.0][23]                               |
| [spdx-maven-plugin Maven Plugin][37]                    | [The Apache Software License, Version 2.0][28] |
| [Build Helper Maven Plugin][38]                         | [The MIT License][39]                          |
| [Apache Maven GPG Plugin][40]                           | [Apache-2.0][23]                               |
| [Central Publishing Maven Plugin][41]                   | [The Apache License, Version 2.0][23]          |
| [Maven Failsafe Plugin][42]                             | [Apache-2.0][23]                               |
| [JaCoCo :: Maven Plugin][43]                            | [EPL-2.0][44]                                  |
| [error-code-crawler-maven-plugin][45]                   | [MIT License][46]                              |
| [Git Commit Id Maven Plugin][47]                        | [GNU Lesser General Public License 3.0][48]    |
| [OpenFastTrace Maven Plugin][49]                        | [GNU General Public License v3.0][50]          |
| [Project Keeper Maven plugin][51]                       | [The MIT License][52]                          |
| [Apache Maven Clean Plugin][53]                         | [Apache-2.0][23]                               |
| [Apache Maven Resources Plugin][54]                     | [Apache-2.0][23]                               |
| [Apache Maven Install Plugin][55]                       | [Apache-2.0][23]                               |
| [Apache Maven Site Plugin][56]                          | [Apache-2.0][23]                               |

[0]: https://github.com/exasol/error-reporting-java/
[1]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[2]: https://github.com/eclipse-ee4j/jsonp
[3]: https://projects.eclipse.org/license/epl-2.0
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[5]: https://projects.eclipse.org/projects/ee4j.jsonb/jakarta.json.bind-api
[6]: https://junit.org/
[7]: https://www.eclipse.org/legal/epl-v20.html
[8]: http://hamcrest.org/JavaHamcrest/
[9]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[10]: https://github.com/mockito/mockito
[11]: https://opensource.org/licenses/MIT
[12]: https://github.com/exasol/exasol-testcontainers/
[13]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[14]: http://www.slf4j.org
[15]: https://opensource.org/license/mit
[16]: https://github.com/eclipse-ee4j/parsson
[17]: https://projects.eclipse.org/projects/ee4j.yasson
[18]: http://www.eclipse.org/legal/epl-v20.html
[19]: http://www.eclipse.org/org/documents/edl-v10.php
[20]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[21]: http://www.gnu.org/licenses/lgpl.txt
[22]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[23]: https://www.apache.org/licenses/LICENSE-2.0.txt
[24]: https://maven.apache.org/plugins/maven-compiler-plugin/
[25]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[26]: https://www.mojohaus.org/flatten-maven-plugin/
[27]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[28]: http://www.apache.org/licenses/LICENSE-2.0.txt
[29]: https://maven.apache.org/surefire/maven-surefire-plugin/
[30]: https://www.mojohaus.org/versions/versions-maven-plugin/
[31]: https://basepom.github.io/duplicate-finder-maven-plugin
[32]: http://www.apache.org/licenses/LICENSE-2.0.html
[33]: https://maven.apache.org/plugins/maven-artifact-plugin/
[34]: https://maven.apache.org/plugins/maven-deploy-plugin/
[35]: https://maven.apache.org/plugins/maven-source-plugin/
[36]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[37]: https://github.com/spdx/spdx-maven-plugin
[38]: https://www.mojohaus.org/build-helper-maven-plugin/
[39]: https://spdx.org/licenses/MIT.txt
[40]: https://maven.apache.org/plugins/maven-gpg-plugin/
[41]: https://central.sonatype.org
[42]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[43]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[44]: https://www.eclipse.org/legal/epl-2.0/
[45]: https://github.com/exasol/error-code-crawler-maven-plugin/
[46]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[47]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[48]: http://www.gnu.org/licenses/lgpl-3.0.txt
[49]: https://github.com/itsallcode/openfasttrace-maven-plugin
[50]: https://www.gnu.org/licenses/gpl-3.0.html
[51]: https://github.com/exasol/project-keeper/
[52]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[53]: https://maven.apache.org/plugins/maven-clean-plugin/
[54]: https://maven.apache.org/plugins/maven-resources-plugin/
[55]: https://maven.apache.org/plugins/maven-install-plugin/
[56]: https://maven.apache.org/plugins/maven-site-plugin/
