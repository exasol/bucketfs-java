<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT License][1]                                                                                             |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [JSON-B API][5]                  | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |

## Test Dependencies

| Dependency                                      | License                          |
| ----------------------------------------------- | -------------------------------- |
| [JUnit Jupiter Engine][6]                       | [Eclipse Public License v2.0][7] |
| [JUnit Jupiter Params][6]                       | [Eclipse Public License v2.0][7] |
| [Hamcrest][8]                                   | [BSD License 3][9]               |
| [mockito-junit-jupiter][10]                     | [MIT][11]                        |
| [Test containers for Exasol on Docker][12]      | [MIT License][13]                |
| [Testcontainers :: JUnit Jupiter Extension][14] | [MIT][15]                        |
| [SLF4J JDK14 Provider][16]                      | [MIT License][17]                |

## Runtime Dependencies

| Dependency            | License                                                                                                      |
| --------------------- | ------------------------------------------------------------------------------------------------------------ |
| [Eclipse Parsson][18] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Yasson][19]          | [Eclipse Public License v. 2.0][20]; [Eclipse Distribution License v. 1.0][21]                               |

## Plugin Dependencies

| Dependency                                              | License                               |
| ------------------------------------------------------- | ------------------------------------- |
| [SonarQube Scanner for Maven][22]                       | [GNU LGPL 3][23]                      |
| [Apache Maven Toolchains Plugin][24]                    | [Apache License, Version 2.0][25]     |
| [OpenFastTrace Maven Plugin][26]                        | [GNU General Public License v3.0][27] |
| [Project Keeper Maven plugin][28]                       | [The MIT License][29]                 |
| [Apache Maven Compiler Plugin][30]                      | [Apache-2.0][25]                      |
| [Apache Maven Enforcer Plugin][31]                      | [Apache-2.0][25]                      |
| [Maven Flatten Plugin][32]                              | [Apache Software Licenese][25]        |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][33] | [ASL2][34]                            |
| [Maven Surefire Plugin][35]                             | [Apache-2.0][25]                      |
| [Versions Maven Plugin][36]                             | [Apache License, Version 2.0][25]     |
| [duplicate-finder-maven-plugin Maven Mojo][37]          | [Apache License 2.0][38]              |
| [Apache Maven Deploy Plugin][39]                        | [Apache-2.0][25]                      |
| [Apache Maven GPG Plugin][40]                           | [Apache-2.0][25]                      |
| [Apache Maven Source Plugin][41]                        | [Apache License, Version 2.0][25]     |
| [Apache Maven Javadoc Plugin][42]                       | [Apache-2.0][25]                      |
| [Nexus Staging Maven Plugin][43]                        | [Eclipse Public License][44]          |
| [Maven Failsafe Plugin][45]                             | [Apache-2.0][25]                      |
| [JaCoCo :: Maven Plugin][46]                            | [Eclipse Public License 2.0][47]      |
| [error-code-crawler-maven-plugin][48]                   | [MIT License][49]                     |
| [Reproducible Build Maven Plugin][50]                   | [Apache 2.0][34]                      |

[0]: https://github.com/exasol/error-reporting-java/
[1]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[2]: https://github.com/eclipse-ee4j/jsonp
[3]: https://projects.eclipse.org/license/epl-2.0
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[5]: https://github.com/eclipse-ee4j/jsonb-api
[6]: https://junit.org/junit5/
[7]: https://www.eclipse.org/legal/epl-v20.html
[8]: http://hamcrest.org/JavaHamcrest/
[9]: http://opensource.org/licenses/BSD-3-Clause
[10]: https://github.com/mockito/mockito
[11]: https://opensource.org/licenses/MIT
[12]: https://github.com/exasol/exasol-testcontainers/
[13]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[14]: https://java.testcontainers.org
[15]: http://opensource.org/licenses/MIT
[16]: http://www.slf4j.org
[17]: http://www.opensource.org/licenses/mit-license.php
[18]: https://github.com/eclipse-ee4j/parsson
[19]: https://projects.eclipse.org/projects/ee4j.yasson
[20]: http://www.eclipse.org/legal/epl-v20.html
[21]: http://www.eclipse.org/org/documents/edl-v10.php
[22]: http://sonarsource.github.io/sonar-scanner-maven/
[23]: http://www.gnu.org/licenses/lgpl.txt
[24]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[25]: https://www.apache.org/licenses/LICENSE-2.0.txt
[26]: https://github.com/itsallcode/openfasttrace-maven-plugin
[27]: https://www.gnu.org/licenses/gpl-3.0.html
[28]: https://github.com/exasol/project-keeper/
[29]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[30]: https://maven.apache.org/plugins/maven-compiler-plugin/
[31]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[32]: https://www.mojohaus.org/flatten-maven-plugin/
[33]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[34]: http://www.apache.org/licenses/LICENSE-2.0.txt
[35]: https://maven.apache.org/surefire/maven-surefire-plugin/
[36]: https://www.mojohaus.org/versions/versions-maven-plugin/
[37]: https://basepom.github.io/duplicate-finder-maven-plugin
[38]: http://www.apache.org/licenses/LICENSE-2.0.html
[39]: https://maven.apache.org/plugins/maven-deploy-plugin/
[40]: https://maven.apache.org/plugins/maven-gpg-plugin/
[41]: https://maven.apache.org/plugins/maven-source-plugin/
[42]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[43]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[44]: http://www.eclipse.org/legal/epl-v10.html
[45]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[46]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[47]: https://www.eclipse.org/legal/epl-2.0/
[48]: https://github.com/exasol/error-code-crawler-maven-plugin/
[49]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[50]: http://zlika.github.io/reproducible-build-maven-plugin
