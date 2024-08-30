/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.DependencyGraphDto
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.DependencyNodeDto

internal class Graph(
    val directDependencies: List<Node>,
) {

    /**
     * @return the number of nodes in the graph
     */
    fun size(): Int {
        return directDependencies.sumOf { 1 + it.size() }
    }

    companion object {
        fun from(dependencyGraphDto: DependencyGraphDto): Graph {

            val nodeToChild: MutableMap<DependencyNodeDto, MutableList<DependencyNodeDto>> = mutableMapOf()

            dependencyGraphDto.edges.forEach { edge ->
                val fromNode = dependencyGraphDto.nodes[edge.from]

                if (!nodeToChild.contains(fromNode)) {
                    nodeToChild[fromNode] = mutableListOf()
                }

                val targetNode = dependencyGraphDto.nodes[edge.to]
                nodeToChild[fromNode]?.add(targetNode)
            }

            fun transformNode(node: DependencyNodeDto, visited: MutableSet<DependencyNodeDto>): Node {

                if (node in visited) {
                    //NB: every cycle in a graph adds this artificial node with the correct
                    // artifact id and used version but without its children.
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

class Node(
    val children: List<Node> = listOf(),
    val artifactIdx: Int,
    val usedVersion: String
) {
    fun size(): Int {
        return children.size + children.sumOf { it.size() }
    }
}
