/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

import org.gradle.accessors.dm.LibrariesForLibs

private val Project.libs: LibrariesForLibs
    get() = extensions.getByType()

if (project != rootProject) version = rootProject.version

plugins {
    // Apply core plugins.
    `java-library`
    id("publish-conventions")
    jacoco
    id("org.jetbrains.dokka")
    id("com.ncorti.ktfmt.gradle")
    kotlin("jvm")
}

repositories { mavenCentral() }

dependencies {
    implementation(libs.bundles.logging)
    testRuntimeOnly(libs.slf4j.logger)
    testImplementation(libs.kotlin.test)
}

// kotlin
java {
    // adds sources and javadoc to build artifacts. This is relevant for publishing
    withSourcesJar()
    withJavadocJar()
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

configurations.all {
    resolutionStrategy {
        // Ensure that all transitive versions of Kotlin libraries match our version of Kotlin.
        force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlinPlugin.get()}")
    }
}

// formatting
ktfmt {
    // KotlinLang style - 4 space indentation - From kotlinlang.org/docs/coding-conventions.html
    kotlinLangStyle()
}

// testing
tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// code coverage
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports { xml.required = true }
}

tasks.register("jacocoReport") {
    description = "Generates code coverage reports for all test tasks."
    group = "Reporting"

    dependsOn(tasks.withType<JacocoReport>())
}

// javadoc
tasks.named<Jar>("javadocJar") { from(tasks.named("dokkaJavadoc")) }
