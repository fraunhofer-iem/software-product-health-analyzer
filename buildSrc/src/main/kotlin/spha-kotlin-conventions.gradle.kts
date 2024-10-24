/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.accessors.dm.LibrariesForLibs

private val Project.libs: LibrariesForLibs
    get() = extensions.getByType()

plugins {
    // Apply core plugins.
    `java-library`
    id("com.vanniktech.maven.publish")
    jacoco
    id("org.jetbrains.dokka")
    id("com.ncorti.ktfmt.gradle")
    kotlin("jvm")
}

if (project != rootProject) version = rootProject.version

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaJavadoc")))

    coordinates(
        groupId = "de.fraunhofer.iem",
        artifactId = "spha-${project.name}",
        version = version.toString(),
    )

    publishToMavenCentral(SonatypeHost.S01, automaticRelease = false)
    signAllPublications()

    pom {
        name = "spha-${project.name}"
        description = "SPHA is a collection of libraries to work with hierarchical KPI models."
        url = "https://github.com/fraunhofer-iem/software-product-health-analyzer"
        version = version.toString()
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

kotlin {
    compilerOptions { apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0) }
}

testing { suites { withType<JvmTestSuite>().configureEach { useJUnitJupiter() } } }

dependencies {
    implementation(libs.bundles.logging)
    testImplementation(libs.slf4j.logger)
    testImplementation(libs.kotlin.test)
}

ktfmt {
    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()
}

configurations.all {
    resolutionStrategy {
        // Ensure that all transitive versions of Kotlin libraries match our version of Kotlin.
        force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlinPlugin.get()}")
    }
}

tasks.withType<Test> { finalizedBy(tasks.jacocoTestReport) }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports { xml.required = true }
}

tasks.register("jacocoReport") {
    description = "Generates code coverage reports for all test tasks."
    group = "Reporting"

    dependsOn(tasks.withType<JacocoReport>())
}
