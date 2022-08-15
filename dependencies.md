<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT][1]                                                                                                     |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [JSON-B API][5]                  | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [org.eclipse.yasson][6]          | [Eclipse Public License v. 2.0][7]; [Eclipse Distribution License v. 1.0][8]                                 |
| [Apache Commons Compress][9]     | [Apache License, Version 2.0][10]                                                                            |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][11]                      | [Eclipse Public License v2.0][12] |
| [JUnit Jupiter Params][11]                      | [Eclipse Public License v2.0][12] |
| [Hamcrest][13]                                  | [BSD License 3][14]               |
| [mockito-junit-jupiter][15]                     | [The MIT License][16]             |
| [Test containers for Exasol on Docker][17]      | [MIT][1]                          |
| [Testcontainers :: JUnit Jupiter Extension][18] | [MIT][19]                         |

## Runtime Dependencies

| Dependency                   | License                                                                                                      |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [JSON-P Default Provider][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][20]                       | [GNU LGPL 3][21]                               |
| [Apache Maven Compiler Plugin][22]                      | [Apache License, Version 2.0][10]              |
| [Apache Maven Enforcer Plugin][23]                      | [Apache License, Version 2.0][10]              |
| [Maven Flatten Plugin][24]                              | [Apache Software Licenese][25]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][26] | [ASL2][25]                                     |
| [Maven Surefire Plugin][27]                             | [Apache License, Version 2.0][10]              |
| [Versions Maven Plugin][28]                             | [Apache License, Version 2.0][10]              |
| [Apache Maven Deploy Plugin][29]                        | [Apache License, Version 2.0][10]              |
| [Apache Maven GPG Plugin][30]                           | [Apache License, Version 2.0][10]              |
| [Apache Maven Source Plugin][31]                        | [Apache License, Version 2.0][10]              |
| [Apache Maven Javadoc Plugin][32]                       | [Apache License, Version 2.0][10]              |
| [Nexus Staging Maven Plugin][33]                        | [Eclipse Public License][34]                   |
| [OpenFastTrace Maven Plugin][35]                        | [GNU General Public License v3.0][36]          |
| [Project keeper maven plugin][37]                       | [The MIT License][38]                          |
| [Maven Failsafe Plugin][39]                             | [Apache License, Version 2.0][10]              |
| [JaCoCo :: Maven Plugin][40]                            | [Eclipse Public License 2.0][41]               |
| [error-code-crawler-maven-plugin][42]                   | [MIT License][43]                              |
| [Reproducible Build Maven Plugin][44]                   | [Apache 2.0][25]                               |
| [Maven Clean Plugin][45]                                | [The Apache Software License, Version 2.0][25] |
| [Maven Resources Plugin][46]                            | [The Apache Software License, Version 2.0][25] |
| [Maven JAR Plugin][47]                                  | [The Apache Software License, Version 2.0][25] |
| [Maven Install Plugin][48]                              | [The Apache Software License, Version 2.0][25] |
| [Maven Site Plugin 3][49]                               | [The Apache Software License, Version 2.0][25] |

[0]: https://github.com/exasol/error-reporting-java
[1]: https://opensource.org/licenses/MIT
[2]: https://github.com/eclipse-ee4j/jsonp
[3]: https://projects.eclipse.org/license/epl-2.0
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[5]: https://github.com/eclipse-ee4j/jsonb-api
[6]: https://projects.eclipse.org/projects/ee4j.yasson
[7]: http://www.eclipse.org/legal/epl-v20.html
[8]: http://www.eclipse.org/org/documents/edl-v10.php
[9]: https://commons.apache.org/proper/commons-compress/
[10]: https://www.apache.org/licenses/LICENSE-2.0.txt
[11]: https://junit.org/junit5/
[12]: https://www.eclipse.org/legal/epl-v20.html
[13]: http://hamcrest.org/JavaHamcrest/
[14]: http://opensource.org/licenses/BSD-3-Clause
[15]: https://github.com/mockito/mockito
[16]: https://github.com/mockito/mockito/blob/main/LICENSE
[17]: https://github.com/exasol/exasol-testcontainers
[18]: https://testcontainers.org
[19]: http://opensource.org/licenses/MIT
[20]: http://sonarsource.github.io/sonar-scanner-maven/
[21]: http://www.gnu.org/licenses/lgpl.txt
[22]: https://maven.apache.org/plugins/maven-compiler-plugin/
[23]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[24]: https://www.mojohaus.org/flatten-maven-plugin/
[25]: http://www.apache.org/licenses/LICENSE-2.0.txt
[26]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[27]: https://maven.apache.org/surefire/maven-surefire-plugin/
[28]: http://www.mojohaus.org/versions-maven-plugin/
[29]: https://maven.apache.org/plugins/maven-deploy-plugin/
[30]: https://maven.apache.org/plugins/maven-gpg-plugin/
[31]: https://maven.apache.org/plugins/maven-source-plugin/
[32]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[33]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[34]: http://www.eclipse.org/legal/epl-v10.html
[35]: https://github.com/itsallcode/openfasttrace-maven-plugin
[36]: https://www.gnu.org/licenses/gpl-3.0.html
[37]: https://github.com/exasol/project-keeper/
[38]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[39]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[40]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[41]: https://www.eclipse.org/legal/epl-2.0/
[42]: https://github.com/exasol/error-code-crawler-maven-plugin/
[43]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[44]: http://zlika.github.io/reproducible-build-maven-plugin
[45]: http://maven.apache.org/plugins/maven-clean-plugin/
[46]: http://maven.apache.org/plugins/maven-resources-plugin/
[47]: http://maven.apache.org/plugins/maven-jar-plugin/
[48]: http://maven.apache.org/plugins/maven-install-plugin/
[49]: http://maven.apache.org/plugins/maven-site-plugin/
