/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.*
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.util.TimeHelper
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.TlcDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.z4kn4fein.semver.toVersion

sealed class TechLagResult {
    data class Success(val libyear: Long) : TechLagResult()
    data class Empty(val reason: String) : TechLagResult()
}

private val logger = KotlinLogging.logger {}

object TlcAdapter : KpiAdapter<TlcDto> {
    override fun transformDataToKpi(data: Collection<TlcDto>): Collection<AdapterResult> {
        return data.flatMap { tlcDto ->
            tlcDto.projectDtos.flatMap {
                val project = Project.from(it)
                project.graph.map { (scope, graph) ->

                    val techLag =
                        getTechLagForGraph(
                            graph = graph,
                            artifacts = project.artifacts,
                            targetVersion = Version.Major
                        )

                    if (techLag is TechLagResult.Success) {

                        val libyearScore = getLibyearScore(techLag.libyear)

                        val rawValueKpi = if (
                            isProductionScope(ecosystem = project.ecosystem, scope = scope)
                        ) {
                            RawValueKpi(
                                score = libyearScore,
                                kind = KpiId.LIB_DAYS_PROD
                            )
                        } else {
                            RawValueKpi(
                                score = libyearScore,
                                kind = KpiId.LIB_DAYS_DEV
                            )
                        }

                        return@map AdapterResult.Success(rawValueKpi)
                    }

                    return@map AdapterResult.Error(ErrorType.DATA_VALIDATION_ERROR)
                }
            }
        }
    }

    private fun getLibyearScore(libyear: Long): Int {
        // NB: these values need to be sanity checked
        // TODO: libyear thresholds should live in a config file
        return when (libyear) {
            in Int.MIN_VALUE..30 -> 100
            in 31..90 -> 75
            in 91..180 -> 50
            in 181..360 -> 25
            else -> 0
        }
    }

    private fun isProductionScope(scope: String, ecosystem: String): Boolean {
        return when (ecosystem) {
            "NPM" -> "dependencies" == scope
            else -> true
        }
    }

    private fun getTechLagForGraph(graph: Graph, artifacts: List<Artifact>, targetVersion: Version): TechLagResult {
        if (graph.directDependencies.isEmpty()) {
            return TechLagResult.Empty(
                reason = "Direct dependencies are empty."
            )
        }

        val sumOfTechLag = graph.directDependencies.sumOf { directDependency ->
            val techLagResult = getTechLagForNode(directDependency, artifacts, targetVersion)
            if (techLagResult is TechLagResult.Success) {
                techLagResult.libyear
            } else {
                logger.warn { "Tech lag result is $techLagResult" }
                0
            }
        }

        return TechLagResult.Success(libyear = sumOfTechLag)
    }

    private fun getTechLagForNode(node: Node, artifacts: List<Artifact>, targetUpdateType: Version): TechLagResult {
        val artifact = artifacts[node.artifactIdx]

        val targetVersion = getTargetVersion(
            usedVersion = node.usedVersion,
            updateType = targetUpdateType,
            versions = artifact.versions
        )
        val usedVersion = artifact.versions.find { it.versionNumber == node.usedVersion }

        if (usedVersion == null || targetVersion == null) {
            return TechLagResult.Empty(
                reason = "Result incomplete. Current version: $usedVersion, target version: $targetVersion."
            )
        }

        val diffInDays = TimeHelper.getDifferenceInDays(
            currentVersion = usedVersion.releaseDate,
            newestVersion = targetVersion.releaseDate
        )

        return TechLagResult.Success(libyear = diffInDays)
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
    internal fun getTargetVersion(
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
