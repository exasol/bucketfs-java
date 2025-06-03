<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT License][1]                                                                                             |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [JSON-B API][5]                  | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Apache Commons Compress][6]     | [Apache-2.0][7]                                                                                              |

## Test Dependencies

| Dependency                                      | License                          |
| ----------------------------------------------- | -------------------------------- |
| [JUnit Jupiter API][8]                          | [Eclipse Public License v2.0][9] |
| [JUnit Jupiter Params][8]                       | [Eclipse Public License v2.0][9] |
| [Hamcrest][10]                                  | [BSD-3-Clause][11]               |
| [mockito-junit-jupiter][12]                     | [MIT][13]                        |
| [Test containers for Exasol on Docker][14]      | [MIT License][15]                |
| [Testcontainers :: JUnit Jupiter Extension][16] | [MIT][17]                        |

## Runtime Dependencies

| Dependency                   | License                                                                                                      |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [JSON-P Default Provider][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Yasson][18]                 | [Eclipse Public License v. 2.0][19]; [Eclipse Distribution License v. 1.0][20]                               |

## Plugin Dependencies

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [Apache Maven Clean Plugin][21]                         | [Apache-2.0][7]                             |
| [Apache Maven Install Plugin][22]                       | [Apache-2.0][7]                             |
| [Apache Maven Resources Plugin][23]                     | [Apache-2.0][7]                             |
| [Apache Maven Site Plugin][24]                          | [Apache-2.0][7]                             |
| [SonarQube Scanner for Maven][25]                       | [GNU LGPL 3][26]                            |
| [Apache Maven Toolchains Plugin][27]                    | [Apache-2.0][7]                             |
| [Apache Maven Compiler Plugin][28]                      | [Apache-2.0][7]                             |
| [Apache Maven Enforcer Plugin][29]                      | [Apache-2.0][7]                             |
| [Maven Flatten Plugin][30]                              | [Apache Software Licenese][7]               |
| [OpenFastTrace Maven Plugin][31]                        | [GNU General Public License v3.0][32]       |
| [Project Keeper Maven plugin][33]                       | [The MIT License][34]                       |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][35] | [ASL2][36]                                  |
| [Maven Surefire Plugin][37]                             | [Apache-2.0][7]                             |
| [Versions Maven Plugin][38]                             | [Apache License, Version 2.0][7]            |
| [duplicate-finder-maven-plugin Maven Mojo][39]          | [Apache License 2.0][40]                    |
| [Apache Maven Artifact Plugin][41]                      | [Apache-2.0][7]                             |
| [Apache Maven Deploy Plugin][42]                        | [Apache-2.0][7]                             |
| [Apache Maven GPG Plugin][43]                           | [Apache-2.0][7]                             |
| [Apache Maven Source Plugin][44]                        | [Apache License, Version 2.0][7]            |
| [Apache Maven Javadoc Plugin][45]                       | [Apache-2.0][7]                             |
| [Nexus Staging Maven Plugin][46]                        | [Eclipse Public License][47]                |
| [Maven Failsafe Plugin][48]                             | [Apache-2.0][7]                             |
| [JaCoCo :: Maven Plugin][49]                            | [EPL-2.0][50]                               |
| [Quality Summarizer Maven Plugin][51]                   | [MIT License][52]                           |
| [error-code-crawler-maven-plugin][53]                   | [MIT License][54]                           |
| [Git Commit Id Maven Plugin][55]                        | [GNU Lesser General Public License 3.0][56] |

[0]: https://github.com/exasol/error-reporting-java/
[1]: https://github.com/exasol/error-reporting-java/blob/main/LICENSE
[2]: https://github.com/eclipse-ee4j/jsonp
[3]: https://projects.eclipse.org/license/epl-2.0
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[5]: https://jakartaee.github.io/jsonb-api
[6]: https://commons.apache.org/proper/commons-compress/
[7]: https://www.apache.org/licenses/LICENSE-2.0.txt
[8]: https://junit.org/junit5/
[9]: https://www.eclipse.org/legal/epl-v20.html
[10]: http://hamcrest.org/JavaHamcrest/
[11]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[12]: https://github.com/mockito/mockito
[13]: https://opensource.org/licenses/MIT
[14]: https://github.com/exasol/exasol-testcontainers/
[15]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[16]: https://java.testcontainers.org
[17]: http://opensource.org/licenses/MIT
[18]: https://projects.eclipse.org/projects/ee4j.yasson
[19]: http://www.eclipse.org/legal/epl-v20.html
[20]: http://www.eclipse.org/org/documents/edl-v10.php
[21]: https://maven.apache.org/plugins/maven-clean-plugin/
[22]: https://maven.apache.org/plugins/maven-install-plugin/
[23]: https://maven.apache.org/plugins/maven-resources-plugin/
[24]: https://maven.apache.org/plugins/maven-site-plugin/
[25]: http://docs.sonarqube.org/display/PLUG/Plugin+Library/sonar-scanner-maven/sonar-maven-plugin
[26]: http://www.gnu.org/licenses/lgpl.txt
[27]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[28]: https://maven.apache.org/plugins/maven-compiler-plugin/
[29]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[30]: https://www.mojohaus.org/flatten-maven-plugin/
[31]: https://github.com/itsallcode/openfasttrace-maven-plugin
[32]: https://www.gnu.org/licenses/gpl-3.0.html
[33]: https://github.com/exasol/project-keeper/
[34]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[35]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[36]: http://www.apache.org/licenses/LICENSE-2.0.txt
[37]: https://maven.apache.org/surefire/maven-surefire-plugin/
[38]: https://www.mojohaus.org/versions/versions-maven-plugin/
[39]: https://basepom.github.io/duplicate-finder-maven-plugin
[40]: http://www.apache.org/licenses/LICENSE-2.0.html
[41]: https://maven.apache.org/plugins/maven-artifact-plugin/
[42]: https://maven.apache.org/plugins/maven-deploy-plugin/
[43]: https://maven.apache.org/plugins/maven-gpg-plugin/
[44]: https://maven.apache.org/plugins/maven-source-plugin/
[45]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[46]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[47]: http://www.eclipse.org/legal/epl-v10.html
[48]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[49]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[50]: https://www.eclipse.org/legal/epl-2.0/
[51]: https://github.com/exasol/quality-summarizer-maven-plugin/
[52]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[53]: https://github.com/exasol/error-code-crawler-maven-plugin/
[54]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[55]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[56]: http://www.gnu.org/licenses/lgpl-3.0.txt
