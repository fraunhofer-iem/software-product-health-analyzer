/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.ArtifactDto
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.DependencyGraphDto
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.DependencyNodeDto
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.ProjectDto

internal data class Project(
    val artifacts: List<ArtifactDto> = listOf(), // Stores all components and their related metadata
    val graph: Map<String, Graph>, // Maps the graphs' scope to the dependency graph extracted from the project
    val ecosystem: String, // Used to identify the appropriate APIs to call for additional information
    val version: String = "",
    val artifactId: String = "",
    val groupId: String = ""
) {
    companion object {
        fun from(projectDto: ProjectDto): Project {
            return Project(
                artifacts = projectDto.artifacts,
                ecosystem = projectDto.ecosystem,
                version = projectDto.version,
                artifactId = projectDto.artifactId,
                groupId = projectDto.groupId,
                graph = projectDto.graph.associate { it.scope to Graph.from(it.graph) },
            )
        }
    }
}

internal data class Graph(
    val directDependencies: List<Node>,
) {
    companion object {
        fun from(dependencyGraphDto: DependencyGraphDto): Graph {

            val nodeToChild: MutableMap<DependencyNodeDto, MutableList<DependencyNodeDto>> = mutableMapOf()

            dependencyGraphDto.edges.forEach { edge ->
                val fromNode = dependencyGraphDto.nodes[edge.from]

                if (!nodeToChild.containsKey(fromNode)) {
                    nodeToChild[fromNode] = mutableListOf()
                }

                val targetNode = dependencyGraphDto.nodes[edge.to]
                nodeToChild[fromNode]?.add(targetNode)
            }

            fun transformNode(node: DependencyNodeDto, visited: MutableSet<DependencyNodeDto>): Node {

                if (node in visited) {
                    return Node(
                        children = listOf(),
                        artifactIdx = node.artifactIdx,
                        usedVersion = node.usedVersion
                    )
                }

                visited.add(node)

                val children = nodeToChild[node]?.map { child ->
                    transformNode(child, visited)
                } ?: listOf()

                return Node(
                    children = children,
                    usedVersion = node.usedVersion,
                    artifactIdx = node.artifactIdx,
                )

            }

            return Graph(
                directDependencies = dependencyGraphDto.directDependencyIndices.map { idx ->
                    val directDependency = dependencyGraphDto.nodes[idx]

                    transformNode(directDependency, mutableSetOf())
                }
            )

        }
    }
}

internal data class Node(
    val children: List<Node> = listOf(),
    val artifactIdx: Int,
    val usedVersion: String
)
