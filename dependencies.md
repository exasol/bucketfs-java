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
| [mockito-junit-jupiter][10]                     | [The MIT License][11]            |
| [Test containers for Exasol on Docker][12]      | [MIT License][13]                |
| [Testcontainers :: JUnit Jupiter Extension][14] | [MIT][15]                        |
| [SLF4J JDK14 Binding][16]                       | [MIT License][17]                |

## Runtime Dependencies

| Dependency            | License                                                                                                      |
| --------------------- | ------------------------------------------------------------------------------------------------------------ |
| [Eclipse Parsson][18] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [Yasson][19]          | [Eclipse Public License v. 2.0][20]; [Eclipse Distribution License v. 1.0][21]                               |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][22]                       | [GNU LGPL 3][23]                               |
| [Apache Maven Compiler Plugin][24]                      | [Apache License, Version 2.0][25]              |
| [Apache Maven Enforcer Plugin][26]                      | [Apache-2.0][25]                               |
| [Maven Flatten Plugin][27]                              | [Apache Software Licenese][25]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][28] | [ASL2][29]                                     |
| [Maven Surefire Plugin][30]                             | [Apache License, Version 2.0][25]              |
| [Versions Maven Plugin][31]                             | [Apache License, Version 2.0][25]              |
| [Apache Maven Deploy Plugin][32]                        | [Apache-2.0][25]                               |
| [Apache Maven GPG Plugin][33]                           | [Apache License, Version 2.0][25]              |
| [Apache Maven Source Plugin][34]                        | [Apache License, Version 2.0][25]              |
| [Apache Maven Javadoc Plugin][35]                       | [Apache License, Version 2.0][25]              |
| [Nexus Staging Maven Plugin][36]                        | [Eclipse Public License][37]                   |
| [Maven Failsafe Plugin][38]                             | [Apache License, Version 2.0][25]              |
| [JaCoCo :: Maven Plugin][39]                            | [Eclipse Public License 2.0][40]               |
| [error-code-crawler-maven-plugin][41]                   | [MIT License][42]                              |
| [Reproducible Build Maven Plugin][43]                   | [Apache 2.0][29]                               |
| [OpenFastTrace Maven Plugin][44]                        | [GNU General Public License v3.0][45]          |
| [Project keeper maven plugin][46]                       | [The MIT License][47]                          |
| [duplicate-finder-maven-plugin Maven Mojo][48]          | [Apache License 2.0][49]                       |
| [Maven Clean Plugin][50]                                | [The Apache Software License, Version 2.0][29] |
| [Maven Resources Plugin][51]                            | [The Apache Software License, Version 2.0][29] |
| [Maven JAR Plugin][52]                                  | [The Apache Software License, Version 2.0][29] |
| [Maven Install Plugin][53]                              | [The Apache Software License, Version 2.0][29] |
| [Maven Site Plugin 3][54]                               | [The Apache Software License, Version 2.0][29] |

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
[11]: https://github.com/mockito/mockito/blob/main/LICENSE
[12]: https://github.com/exasol/exasol-testcontainers/
[13]: https://github.com/exasol/exasol-testcontainers/blob/main/LICENSE
[14]: https://testcontainers.org
[15]: http://opensource.org/licenses/MIT
[16]: http://www.slf4j.org
[17]: http://www.opensource.org/licenses/mit-license.php
[18]: https://github.com/eclipse-ee4j/parsson
[19]: https://projects.eclipse.org/projects/ee4j.yasson
[20]: http://www.eclipse.org/legal/epl-v20.html
[21]: http://www.eclipse.org/org/documents/edl-v10.php
[22]: http://sonarsource.github.io/sonar-scanner-maven/
[23]: http://www.gnu.org/licenses/lgpl.txt
[24]: https://maven.apache.org/plugins/maven-compiler-plugin/
[25]: https://www.apache.org/licenses/LICENSE-2.0.txt
[26]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[27]: https://www.mojohaus.org/flatten-maven-plugin/
[28]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[29]: http://www.apache.org/licenses/LICENSE-2.0.txt
[30]: https://maven.apache.org/surefire/maven-surefire-plugin/
[31]: https://www.mojohaus.org/versions/versions-maven-plugin/
[32]: https://maven.apache.org/plugins/maven-deploy-plugin/
[33]: https://maven.apache.org/plugins/maven-gpg-plugin/
[34]: https://maven.apache.org/plugins/maven-source-plugin/
[35]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[36]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[37]: http://www.eclipse.org/legal/epl-v10.html
[38]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[39]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[40]: https://www.eclipse.org/legal/epl-2.0/
[41]: https://github.com/exasol/error-code-crawler-maven-plugin/
[42]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[43]: http://zlika.github.io/reproducible-build-maven-plugin
[44]: https://github.com/itsallcode/openfasttrace-maven-plugin
[45]: https://www.gnu.org/licenses/gpl-3.0.html
[46]: https://github.com/exasol/project-keeper/
[47]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[48]: https://github.com/basepom/duplicate-finder-maven-plugin
[49]: http://www.apache.org/licenses/LICENSE-2.0.html
[50]: http://maven.apache.org/plugins/maven-clean-plugin/
[51]: http://maven.apache.org/plugins/maven-resources-plugin/
[52]: http://maven.apache.org/plugins/maven-jar-plugin/
[53]: http://maven.apache.org/plugins/maven-install-plugin/
[54]: http://maven.apache.org/plugins/maven-site-plugin/
