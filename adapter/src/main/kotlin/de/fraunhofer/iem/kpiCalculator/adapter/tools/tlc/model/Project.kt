/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.ProjectDto


internal data class Project(
    val artifacts: List<Artifact> = listOf(), // Stores all components and their related metadata
    val graph: Map<String, Graph>, // Maps the graphs' scope to the dependency graph extracted from the project
    val ecosystem: String, // Used to identify the appropriate APIs to call for additional information
    val version: String = "",
    val artifactId: String = "",
    val groupId: String = ""
) {
    companion object {
        fun from(projectDto: ProjectDto): Project {
            return Project(
                artifacts = projectDto.artifacts.map {
                    Artifact(
                        artifactId = it.artifactId,
                        groupId = it.groupId,
                        versions = it.versions.map { version ->
                            ArtifactVersion.create(
                                versionNumber = version.versionNumber,
                                isDefault = version.isDefault,
                                releaseDate = version.releaseDate,
                            )
                        },
                    )
                },
                ecosystem = projectDto.ecosystem,
                version = projectDto.version,
                artifactId = projectDto.artifactId,
                groupId = projectDto.groupId,
                graph = projectDto.graph.associate { it.scope to Graph.from(it.graph) },
            )
        }
    }
}
