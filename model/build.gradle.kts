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



group = "de.fraunhofer.iem.kpiCalculator"

dependencies {
    implementation(libs.kotlin.serialization.json)
}

tasks.test {
    useJUnitPlatform()
}
