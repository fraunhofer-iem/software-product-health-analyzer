import com.vanniktech.maven.publish.SonatypeHost

/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

plugins {
    // Apply third-party plugins.
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    coordinates(groupId = "de.fraunhofer.iem")

    publishToMavenCentral(SonatypeHost.S01)

    pom {
        name = project.name
        description =
            "SPHA is a library to transform tool results into KPIs and calculate KPI hierarchies."
        url = "https://github.com/fraunhofer-iem/software-product-health-analyzer"

        developers {
            developer {
                name = "Fraunhofer IEM"
                email = "jan-niclas.struewer@iem.fraunhofer.de"
                url =
                    "https://github.com/fraunhofer-iem/software-product-health-analyzer/graphs/contributors"
            }
        }

        licenses {
            license {
                name = "MIT"
                url =
                    "https://github.com/fraunhofer-iem/software-product-health-analyzer/blob/main/LICENSE.md"
            }
        }

        scm {
            connection =
                "scm:git:https://github.com/fraunhofer-iem/software-product-health-analyzer.git"
            developerConnection =
                "scm:git:git@github.com:fraunhofer-iem/software-product-health-analyzer.git"
            tag = version.toString()
            url = "https://github.com/fraunhofer-iem/software-product-health-analyzer"
        }
    }
}
