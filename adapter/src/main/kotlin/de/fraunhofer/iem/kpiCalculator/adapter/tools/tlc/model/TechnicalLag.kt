/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

data class TechnicalLag(
    val version: String,
    val libDays: Long,
    val releaseDistance: Triple<Int, Int, Int>,
    val releaseFrequency: ReleaseFrequency,
    val numberOfMissedReleases: Int
) {
    override fun toString(): String {
        return "Technical Lag: libDays: $libDays, " +
                "target version: $version," +
                " # missed releases: $numberOfMissedReleases, " +
                "Version distance ${releaseDistance.first}.${releaseDistance.second}.${releaseDistance.third} " +
                "Release frequency: $releaseFrequency"
    }
}

data class ReleaseFrequency(
    val releasesPerDay: Double,
    val releasesPerWeek: Double,
    val releasesPerMonth: Double
)