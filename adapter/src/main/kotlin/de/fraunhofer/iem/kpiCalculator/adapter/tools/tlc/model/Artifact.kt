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

internal class ArtifactVersion private constructor(
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

        /**
         * If the used version is stable all pre-release versions are ignored.
         * If the used version is pre-release, pre-release versions are
         * also considered.
         *
         * @param usedVersion version currently in use
         * @param updateType do we try to update to the largest
         * patch, minor or major version
         * @param versions all available versions for the artifact
         *
         * @return highest applicable version
         */
        fun getTargetVersion(
            usedVersion: String,
            updateType: Version,
            versions: List<ArtifactVersion>,
        ): ArtifactVersion? {

            val semvers = versions.map { it.semver }
            val current = usedVersion.toVersion(strict = false)

            val filteredVersions = if (current.isStable) {
                semvers.filter { it.isStable && !it.isPreRelease }
            } else {
                if (current.isPreRelease) {
                    semvers
                } else {
                    semvers.filter { !it.isPreRelease }
                }
            }

            val highestVersion = when (updateType) {
                Version.Minor -> {
                    filteredVersions.filter { it.major == current.major }
                }

                Version.Major -> {
                    filteredVersions
                }

                Version.Patch -> {
                    filteredVersions.filter { it.major == current.major && it.minor == current.minor }
                }
            }.max()

            return versions.find { it.versionNumber == highestVersion.toString() }
        }
    }
}
