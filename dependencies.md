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
| [JUnit Jupiter API][6]                          | [Eclipse Public License v2.0][7] |
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

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [Apache Maven Clean Plugin][22]                         | [Apache-2.0][23]                            |
| [Apache Maven Install Plugin][24]                       | [Apache-2.0][23]                            |
| [Apache Maven Resources Plugin][25]                     | [Apache-2.0][23]                            |
| [Apache Maven Site Plugin][26]                          | [Apache-2.0][23]                            |
| [SonarQube Scanner for Maven][27]                       | [GNU LGPL 3][28]                            |
| [Apache Maven Toolchains Plugin][29]                    | [Apache-2.0][23]                            |
| [Apache Maven Compiler Plugin][30]                      | [Apache-2.0][23]                            |
| [Apache Maven Enforcer Plugin][31]                      | [Apache-2.0][23]                            |
| [Maven Flatten Plugin][32]                              | [Apache Software Licenese][23]              |
| [OpenFastTrace Maven Plugin][33]                        | [GNU General Public License v3.0][34]       |
| [Project Keeper Maven plugin][35]                       | [The MIT License][36]                       |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][37] | [ASL2][38]                                  |
| [Maven Surefire Plugin][39]                             | [Apache-2.0][23]                            |
| [Versions Maven Plugin][40]                             | [Apache License, Version 2.0][23]           |
| [duplicate-finder-maven-plugin Maven Mojo][41]          | [Apache License 2.0][42]                    |
| [Apache Maven Artifact Plugin][43]                      | [Apache-2.0][23]                            |
| [Apache Maven Deploy Plugin][44]                        | [Apache-2.0][23]                            |
| [Apache Maven GPG Plugin][45]                           | [Apache-2.0][23]                            |
| [Apache Maven Source Plugin][46]                        | [Apache License, Version 2.0][23]           |
| [Apache Maven Javadoc Plugin][47]                       | [Apache-2.0][23]                            |
| [Central Publishing Maven Plugin][48]                   | [The Apache License, Version 2.0][23]       |
| [Maven Failsafe Plugin][49]                             | [Apache-2.0][23]                            |
| [JaCoCo :: Maven Plugin][50]                            | [EPL-2.0][51]                               |
| [Quality Summarizer Maven Plugin][52]                   | [MIT License][53]                           |
| [error-code-crawler-maven-plugin][54]                   | [MIT License][55]                           |
| [Git Commit Id Maven Plugin][56]                        | [GNU Lesser General Public License 3.0][57] |

[0]: https://github.com/exasol/error-reporting-java/
[1]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[2]: https://github.com/eclipse-ee4j/jsonp
[3]: https://projects.eclipse.org/license/epl-2.0
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[5]: https://jakartaee.github.io/jsonb-api
[6]: https://junit.org/junit5/
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
[22]: https://maven.apache.org/plugins/maven-clean-plugin/
[23]: https://www.apache.org/licenses/LICENSE-2.0.txt
[24]: https://maven.apache.org/plugins/maven-install-plugin/
[25]: https://maven.apache.org/plugins/maven-resources-plugin/
[26]: https://maven.apache.org/plugins/maven-site-plugin/
[27]: http://docs.sonarqube.org/display/PLUG/Plugin+Library/sonar-scanner-maven/sonar-maven-plugin
[28]: http://www.gnu.org/licenses/lgpl.txt
[29]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[30]: https://maven.apache.org/plugins/maven-compiler-plugin/
[31]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[32]: https://www.mojohaus.org/flatten-maven-plugin/
[33]: https://github.com/itsallcode/openfasttrace-maven-plugin
[34]: https://www.gnu.org/licenses/gpl-3.0.html
[35]: https://github.com/exasol/project-keeper/
[36]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[37]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[38]: http://www.apache.org/licenses/LICENSE-2.0.txt
[39]: https://maven.apache.org/surefire/maven-surefire-plugin/
[40]: https://www.mojohaus.org/versions/versions-maven-plugin/
[41]: https://basepom.github.io/duplicate-finder-maven-plugin
[42]: http://www.apache.org/licenses/LICENSE-2.0.html
[43]: https://maven.apache.org/plugins/maven-artifact-plugin/
[44]: https://maven.apache.org/plugins/maven-deploy-plugin/
[45]: https://maven.apache.org/plugins/maven-gpg-plugin/
[46]: https://maven.apache.org/plugins/maven-source-plugin/
[47]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[48]: https://central.sonatype.org
[49]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[50]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[51]: https://www.eclipse.org/legal/epl-2.0/
[52]: https://github.com/exasol/quality-summarizer-maven-plugin/
[53]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[54]: https://github.com/exasol/error-code-crawler-maven-plugin/
[55]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[56]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[57]: http://www.gnu.org/licenses/lgpl-3.0.txt
