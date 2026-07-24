<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT License][1]                                                                                             |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Jakarta JSON Binding API][5]    | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |

## Test Dependencies

| Dependency                                      | License                          |
| ----------------------------------------------- | -------------------------------- |
| [JUnit Jupiter Params][6]                       | [Eclipse Public License v2.0][7] |
| [Hamcrest][8]                                   | [BSD-3-Clause][9]                |
| [mockito-junit-jupiter][10]                     | [MIT][11]                        |
| [Test containers for Exasol on Docker][12]      | [MIT License][13]                |
| [Testcontainers :: JUnit Jupiter Extension][14] | [MIT][15]                        |
| [SLF4J JDK14 Provider][16]                      | [MIT][17]                        |

## Runtime Dependencies

| Dependency            | License                                                                                                      |
| --------------------- | ------------------------------------------------------------------------------------------------------------ |
| [Eclipse Parsson][18] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Yasson][19]          | [Eclipse Public License v. 2.0][20]; [Eclipse Distribution License v. 1.0][21]                               |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][22]                       | [GNU LGPL 3][23]                               |
| [Apache Maven Toolchains Plugin][24]                    | [Apache-2.0][25]                               |
| [Apache Maven Compiler Plugin][26]                      | [Apache-2.0][25]                               |
| [Apache Maven Enforcer Plugin][27]                      | [Apache-2.0][25]                               |
| [Maven Flatten Plugin][28]                              | [Apache Software License][25]                  |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][29] | [ASL2][30]                                     |
| [Maven Surefire Plugin][31]                             | [Apache-2.0][25]                               |
| [Versions Maven Plugin][32]                             | [Apache License, Version 2.0][25]              |
| [duplicate-finder-maven-plugin Maven Mojo][33]          | [Apache License 2.0][34]                       |
| [Apache Maven Artifact Plugin][35]                      | [Apache-2.0][25]                               |
| [Apache Maven Deploy Plugin][36]                        | [Apache-2.0][25]                               |
| [Apache Maven Source Plugin][37]                        | [Apache-2.0][25]                               |
| [Apache Maven Javadoc Plugin][38]                       | [Apache-2.0][25]                               |
| [spdx-maven-plugin Maven Plugin][39]                    | [The Apache Software License, Version 2.0][30] |
| [Build Helper Maven Plugin][40]                         | [The MIT License][41]                          |
| [Apache Maven GPG Plugin][42]                           | [Apache-2.0][25]                               |
| [Central Publishing Maven Plugin][43]                   | [The Apache License, Version 2.0][25]          |
| [Maven Failsafe Plugin][44]                             | [Apache-2.0][25]                               |
| [JaCoCo :: Maven Plugin][45]                            | [EPL-2.0][46]                                  |
| [error-code-crawler-maven-plugin][47]                   | [MIT License][48]                              |
| [Git Commit Id Maven Plugin][49]                        | [GNU Lesser General Public License 3.0][50]    |
| [OpenFastTrace Maven Plugin][51]                        | [GNU General Public License v3.0][52]          |
| [Project Keeper Maven plugin][53]                       | [The MIT License][54]                          |
| [Apache Maven Clean Plugin][55]                         | [Apache-2.0][25]                               |
| [Apache Maven Resources Plugin][56]                     | [Apache-2.0][25]                               |
| [Apache Maven Install Plugin][57]                       | [Apache-2.0][25]                               |
| [Apache Maven Site Plugin][58]                          | [Apache-2.0][25]                               |

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
[14]: https://java.testcontainers.org
[15]: http://opensource.org/licenses/MIT
[16]: http://www.slf4j.org
[17]: https://opensource.org/license/mit
[18]: https://github.com/eclipse-ee4j/parsson
[19]: https://projects.eclipse.org/projects/ee4j.yasson
[20]: http://www.eclipse.org/legal/epl-v20.html
[21]: http://www.eclipse.org/org/documents/edl-v10.php
[22]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[23]: http://www.gnu.org/licenses/lgpl.txt
[24]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[25]: https://www.apache.org/licenses/LICENSE-2.0.txt
[26]: https://maven.apache.org/plugins/maven-compiler-plugin/
[27]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[28]: https://www.mojohaus.org/flatten-maven-plugin/
[29]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[30]: http://www.apache.org/licenses/LICENSE-2.0.txt
[31]: https://maven.apache.org/surefire/maven-surefire-plugin/
[32]: https://www.mojohaus.org/versions/versions-maven-plugin/
[33]: https://basepom.github.io/duplicate-finder-maven-plugin
[34]: http://www.apache.org/licenses/LICENSE-2.0.html
[35]: https://maven.apache.org/plugins/maven-artifact-plugin/
[36]: https://maven.apache.org/plugins/maven-deploy-plugin/
[37]: https://maven.apache.org/plugins/maven-source-plugin/
[38]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[39]: https://github.com/spdx/spdx-maven-plugin
[40]: https://www.mojohaus.org/build-helper-maven-plugin/
[41]: https://spdx.org/licenses/MIT.txt
[42]: https://maven.apache.org/plugins/maven-gpg-plugin/
[43]: https://central.sonatype.org
[44]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[45]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[46]: https://www.eclipse.org/legal/epl-2.0/
[47]: https://github.com/exasol/error-code-crawler-maven-plugin/
[48]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[49]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[50]: http://www.gnu.org/licenses/lgpl-3.0.txt
[51]: https://github.com/itsallcode/openfasttrace-maven-plugin
[52]: https://www.gnu.org/licenses/gpl-3.0.html
[53]: https://github.com/exasol/project-keeper/
[54]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[55]: https://maven.apache.org/plugins/maven-clean-plugin/
[56]: https://maven.apache.org/plugins/maven-resources-plugin/
[57]: https://maven.apache.org/plugins/maven-install-plugin/
[58]: https://maven.apache.org/plugins/maven-site-plugin/
