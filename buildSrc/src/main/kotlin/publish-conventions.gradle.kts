/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

import org.gradle.kotlin.dsl.*

plugins {
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name = "spha-${project.name}"
                description =
                    "SPHA is a collection of libraries to work with hierarchical KPI models."
                url = "https://github.com/fraunhofer-iem/software-product-health-analyzer"
                licenses {
                    license {
                        name = "MIT License"
                        url =
                            "https://github.com/fraunhofer-iem/software-product-health-analyzer/blob/main/LICENSE.md"
                    }
                }
                developers {
                    developer {
                        name = "Jan-Niclas Struewer"
                        email = "jan-niclas.struewer@iem.fraunhofer.de"
                    }
                    developer {
                        name = "Sebastian Leuer"
                        email = "sebastian.leuer@iem.fraunhofer.de"
                    }
                }
                scm {
                    connection =
                        "scm:git:git@github.com:fraunhofer-iem/software-product-health-analyzer.git"
                    developerConnection =
                        "scm:git:ssh://github.com:fraunhofer-iem/software-product-health-analyzer.git"
                    url = "https://github.com/fraunhofer-iem/software-product-health-analyzer"
                }
            }
            groupId = "de.fraunhofer.iem"
            artifactId = "spha-${project.name}"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "mavenCentral"
            val releasesRepoUrl =
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url =
                uri(
                    if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl
                    else releasesRepoUrl
                )
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}
