/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion

internal data class Artifact(
    val artifactId: String,
    val groupId: String,
    val versions: List<ArtifactVersion> = listOf(),
)

private val logger = KotlinLogging.logger {}

@ConsistentCopyVisibility
internal data class ArtifactVersion private constructor(
    val versionNumber: String,
    val releaseDate: Long,
    val isDefault: Boolean = false
) : Comparable<ArtifactVersion> {

    val semver by lazy {
        versionNumber.toVersion(strict = false)
    }

    override fun compareTo(other: ArtifactVersion): Int = semver.compareTo(other.semver)

    companion object {
        fun create(versionNumber: String, releaseDate: Long, isDefault: Boolean = false): ArtifactVersion? {
            return try {
                ArtifactVersion(
                    releaseDate = releaseDate,
                    isDefault = isDefault,
                    // this step harmonizes possibly weired version formats like 2.4 or 5
                    // those are parsed to 2.4.0 and 5.0.0
                    versionNumber = validateAndHarmonizeVersionString(versionNumber)
                )
            } catch (e: VersionFormatException) {
                logger.warn { "Version cast failed with ${e.message}." }
                null
            }
        }

        fun validateAndHarmonizeVersionString(version: String): String {
            return version.toVersion(strict = false).toString()
        }
    }
}
