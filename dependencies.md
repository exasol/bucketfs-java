<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                       | License                                                                                                      |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [error-reporting-java][0]        | [MIT][1]                                                                                                     |
| [Jakarta JSON Processing API][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [JSON-B API][5]                  | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |
| [org.eclipse.yasson][8]          | [Eclipse Public License v. 2.0][9]; [Eclipse Distribution License v. 1.0][10]                                |
| [Apache Commons Compress][11]    | [Apache License, Version 2.0][12]                                                                            |

## Test Dependencies

| Dependency                                      | License                           |
| ----------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][13]                      | [Eclipse Public License v2.0][14] |
| [JUnit Jupiter Params][13]                      | [Eclipse Public License v2.0][14] |
| [Hamcrest][17]                                  | [BSD License 3][18]               |
| [mockito-junit-jupiter][19]                     | [The MIT License][20]             |
| [Test containers for Exasol on Docker][21]      | [MIT][1]                          |
| [Testcontainers :: JUnit Jupiter Extension][23] | [MIT][24]                         |

## Runtime Dependencies

| Dependency                   | License                                                                                                      |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------ |
| [JSON-P Default Provider][2] | [Eclipse Public License 2.0][3]; [GNU General Public License, version 2 with the GNU Classpath Exception][4] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][28]                       | [GNU LGPL 3][29]                               |
| [Apache Maven Compiler Plugin][30]                      | [Apache License, Version 2.0][12]              |
| [Apache Maven Enforcer Plugin][32]                      | [Apache License, Version 2.0][12]              |
| [Maven Flatten Plugin][34]                              | [Apache Software Licenese][35]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][36] | [ASL2][35]                                     |
| [Reproducible Build Maven Plugin][38]                   | [Apache 2.0][35]                               |
| [Maven Surefire Plugin][40]                             | [Apache License, Version 2.0][12]              |
| [Versions Maven Plugin][42]                             | [Apache License, Version 2.0][12]              |
| [Apache Maven Deploy Plugin][44]                        | [Apache License, Version 2.0][12]              |
| [Apache Maven GPG Plugin][46]                           | [Apache License, Version 2.0][12]              |
| [Apache Maven Source Plugin][48]                        | [Apache License, Version 2.0][12]              |
| [Apache Maven Javadoc Plugin][50]                       | [Apache License, Version 2.0][12]              |
| [Nexus Staging Maven Plugin][52]                        | [Eclipse Public License][53]                   |
| [OpenFastTrace Maven Plugin][54]                        | [GNU General Public License v3.0][55]          |
| [Project keeper maven plugin][56]                       | [The MIT License][57]                          |
| [Maven Failsafe Plugin][58]                             | [Apache License, Version 2.0][12]              |
| [JaCoCo :: Maven Plugin][60]                            | [Eclipse Public License 2.0][61]               |
| [error-code-crawler-maven-plugin][62]                   | [MIT][1]                                       |
| [Maven Clean Plugin][64]                                | [The Apache Software License, Version 2.0][35] |
| [Maven Resources Plugin][66]                            | [The Apache Software License, Version 2.0][35] |
| [Maven JAR Plugin][68]                                  | [The Apache Software License, Version 2.0][35] |
| [Maven Install Plugin][70]                              | [The Apache Software License, Version 2.0][35] |
| [Maven Site Plugin 3][72]                               | [The Apache Software License, Version 2.0][35] |

[0]: https://github.com/exasol/error-reporting-java
[9]: http://www.eclipse.org/legal/epl-v20.html
[35]: http://www.apache.org/licenses/LICENSE-2.0.txt
[40]: https://maven.apache.org/surefire/maven-surefire-plugin/
[64]: http://maven.apache.org/plugins/maven-clean-plugin/
[1]: https://opensource.org/licenses/MIT
[19]: https://github.com/mockito/mockito
[11]: https://commons.apache.org/proper/commons-compress/
[42]: http://www.mojohaus.org/versions-maven-plugin/
[56]: https://github.com/exasol/project-keeper/
[18]: http://opensource.org/licenses/BSD-3-Clause
[30]: https://maven.apache.org/plugins/maven-compiler-plugin/
[54]: https://github.com/itsallcode/openfasttrace-maven-plugin
[61]: https://www.eclipse.org/legal/epl-2.0/
[44]: https://maven.apache.org/plugins/maven-deploy-plugin/
[29]: http://www.gnu.org/licenses/lgpl.txt
[60]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[20]: https://github.com/mockito/mockito/blob/main/LICENSE
[38]: http://zlika.github.io/reproducible-build-maven-plugin
[28]: http://sonarsource.github.io/sonar-scanner-maven/
[13]: https://junit.org/junit5/
[34]: https://www.mojohaus.org/flatten-maven-plugin/flatten-maven-plugin
[2]: https://github.com/eclipse-ee4j/jsonp
[48]: https://maven.apache.org/plugins/maven-source-plugin/
[5]: https://github.com/eclipse-ee4j/jsonb-api
[4]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[17]: http://hamcrest.org/JavaHamcrest/
[66]: http://maven.apache.org/plugins/maven-resources-plugin/
[52]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[8]: https://projects.eclipse.org/projects/ee4j.yasson
[58]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[24]: http://opensource.org/licenses/MIT
[53]: http://www.eclipse.org/legal/epl-v10.html
[21]: https://github.com/exasol/exasol-testcontainers
[57]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[55]: https://www.gnu.org/licenses/gpl-3.0.html
[68]: http://maven.apache.org/plugins/maven-jar-plugin/
[3]: https://projects.eclipse.org/license/epl-2.0
[10]: http://www.eclipse.org/org/documents/edl-v10.php
[12]: https://www.apache.org/licenses/LICENSE-2.0.txt
[32]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[14]: https://www.eclipse.org/legal/epl-v20.html
[70]: http://maven.apache.org/plugins/maven-install-plugin/
[36]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[46]: https://maven.apache.org/plugins/maven-gpg-plugin/
[23]: https://testcontainers.org
[72]: http://maven.apache.org/plugins/maven-site-plugin/
[50]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[62]: https://github.com/exasol/error-code-crawler-maven-plugin
