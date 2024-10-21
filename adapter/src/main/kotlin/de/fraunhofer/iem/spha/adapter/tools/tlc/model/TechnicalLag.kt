/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.adapter.tools.tlc.model

data class ReleaseDistance(val patch: Int, val minor: Int, val major: Int)

data class TechnicalLag(
    val version: String,
    val libDays: Long,
    val releaseDistance: ReleaseDistance,
    val releaseFrequency: ReleaseFrequency,
    val numberOfMissedReleases: Int,
) {
    override fun toString(): String {
        return "Technical Lag: libDays: $libDays, " +
            "target version: $version," +
            " # missed releases: $numberOfMissedReleases, " +
            "Version distance ${releaseDistance.major}.${releaseDistance.minor}.${releaseDistance.patch} " +
            "Release frequency: $releaseFrequency"
    }
}

data class ReleaseFrequency(
    val releasesPerDay: Double,
    val releasesPerWeek: Double,
    val releasesPerMonth: Double,
)
