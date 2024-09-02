/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.util

import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.TechLagResult
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Artifact
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.ArtifactVersion.Companion.getTargetVersion
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Graph
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Node
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Version
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal object TechLagHelper {
    fun getTechLagForGraph(graph: Graph, artifacts: List<Artifact>, targetVersion: Version): TechLagResult {
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
}
