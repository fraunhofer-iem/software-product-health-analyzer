/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

plugins {
    id("spha-kotlin-conventions")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(project(":model"))
    implementation(libs.semver)
    implementation(libs.kotlin.serialization.json)

    testImplementation(libs.test.junit5.params)
    testImplementation(libs.test.mockk)
    testImplementation(libs.kotlinx.date)
}
