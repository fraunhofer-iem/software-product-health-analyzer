/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core

import de.fraunhofer.iem.kpiCalculator.core.hierarchy.KpiHierarchyNode
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultHierarchy

private data class TreeUpdate(
    val parent: KpiHierarchyNode,
    val currentNode: KpiHierarchyNode,
    val rawValueKpis: List<RawValueKpi>,
)

object KpiCalculator {
    // XXX: Setup Logger

    fun calculateKpis(
        hierarchy: KpiHierarchy,
        rawValueKpis: List<RawValueKpi>,
    ): KpiResultHierarchy {
        val root = hierarchy.rootNode
        val connectedHierarchyRoot = connectKpiHierarchyToRawValues(root, rawValueKpis)
        depthFirstTraversal(connectedHierarchyRoot) { it.calculateKpi() }

        return KpiResultHierarchy.create(KpiHierarchyNode.to(connectedHierarchyRoot))
    }

    private fun depthFirstTraversal(
        node: KpiHierarchyNode,
        seen: MutableSet<KpiHierarchyNode> = mutableSetOf(),
        action: (node: KpiHierarchyNode) -> Unit,
    ) {
        if (seen.contains(node)) {
            return
        }

        node.hierarchyEdges.forEach { child ->
            depthFirstTraversal(node = child.to, seen = seen, action)
        }

        action(node)
        seen.add(node)
    }

    private fun connectKpiHierarchyToRawValues(
        node: KpiNode,
        rawValueKpis: List<RawValueKpi>,
    ): KpiHierarchyNode {

        val kindToValues = mutableMapOf<KpiId, MutableList<RawValueKpi>>()
        KpiId.entries.forEach { kindToValues[it] = mutableListOf() }

        rawValueKpis.forEach { kindToValues[it.kind]!!.add(it) }

        val calculationRoot = KpiHierarchyNode.from(node)
        val updates: MutableList<TreeUpdate> = mutableListOf()

        depthFirstTraversal(node = calculationRoot) { currentNode ->
            val correspondingRawValue = kindToValues[currentNode.kpiId]
            val parent = currentNode.parent

            if (!correspondingRawValue.isNullOrEmpty() && parent != null) {
                updates.add(TreeUpdate(parent, currentNode, correspondingRawValue))
            }
        }

        updates.forEach {
            val (parent, currentNode, correspondingRawValue) = it

            parent.getWeight(currentNode)?.let { plannedWeight ->
                parent.removeChild(currentNode)

                val rawValueNodes =
                    correspondingRawValue.map { rawValue ->
                        val newNode =
                            KpiHierarchyNode(
                                kpiId = rawValue.kind,
                                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                parent = parent,
                            )
                        newNode.setResult(rawValue.score)
                        newNode
                    }

                if (rawValueNodes.isNotEmpty()) {
                    val weights = plannedWeight / rawValueNodes.size
                    rawValueNodes.forEach { rawValueNode -> parent.addChild(rawValueNode, weights) }
                }
            }
        }

        return calculationRoot
    }
}
