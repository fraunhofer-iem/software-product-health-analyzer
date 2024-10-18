/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultNode

internal class KpiHierarchyNode
private constructor(
    val kpiId: KpiId,
    val kpiStrategyId: KpiStrategyId,
    val hierarchyEdges: List<KpiHierarchyEdge>,
) {

    var result: KpiCalculationResult = KpiCalculationResult.Empty()

    val score: Int
        get() =
            (result as? KpiCalculationResult.Success)?.score
                ?: (result as? KpiCalculationResult.Incomplete)?.score
                ?: 0

    fun hasNoResult(): Boolean {
        return (result is KpiCalculationResult.Empty) || (result is KpiCalculationResult.Error)
    }

    fun hasIncompleteResult(): Boolean {
        return result is KpiCalculationResult.Incomplete
    }

    companion object {
        fun to(node: KpiHierarchyNode): KpiResultNode {
            return KpiResultNode(
                kpiId = node.kpiId,
                strategyType = node.kpiStrategyId,
                kpiResult = node.result,
                children =
                    node.hierarchyEdges.map {
                        KpiResultEdge(
                            target = to(it.to),
                            plannedWeight = it.plannedWeight,
                            actualWeight = it.actualWeight,
                        )
                    },
            )
        }

        fun from(node: KpiNode, rawValueKpis: List<RawValueKpi>): KpiHierarchyNode {
            val kpiIdToValues = mutableMapOf<KpiId, MutableList<RawValueKpi>>()
            KpiId.entries.forEach { kpiIdToValues[it] = mutableListOf() }

            rawValueKpis.forEach { kpiIdToValues[it.kind]!!.add(it) }

            val hierarchy = from(node, kpiIdToValues = kpiIdToValues)

            return hierarchy
        }

        private fun from(
            node: KpiNode,
            kpiIdToValues: Map<KpiId, List<RawValueKpi>>,
        ): KpiHierarchyNode {

            val children: MutableList<KpiHierarchyEdge> = mutableListOf()
            node.edges.forEach { child ->
                val rawValues = kpiIdToValues[child.target.kpiId] ?: emptyList()
                if (rawValues.isNotEmpty()) {
                    rawValues.forEach { rawValueKpi ->
                        val hierarchyNode =
                            KpiHierarchyNode(
                                kpiId = child.target.kpiId,
                                // we force the kpi strategy to be raw value if we had a
                                // raw value for the given node.
                                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                hierarchyEdges = emptyList(),
                            )
                        hierarchyNode.result = KpiCalculationResult.Success(rawValueKpi.score)
                        val edge =
                            KpiHierarchyEdge(
                                to = hierarchyNode,
                                plannedWeight = child.weight / rawValues.count(),
                            )
                        children.add(edge)
                    }
                } else {
                    children.add(
                        KpiHierarchyEdge(
                            to = from(child.target, kpiIdToValues),
                            plannedWeight = child.weight,
                        )
                    )
                }
            }

            val calcNode =
                KpiHierarchyNode(
                    kpiId = node.kpiId,
                    hierarchyEdges = children,
                    kpiStrategyId = node.kpiStrategyId,
                )

            return calcNode
        }

        fun depthFirstTraversal(
            node: KpiHierarchyNode,
            seen: MutableSet<KpiHierarchyNode> = mutableSetOf(),
            action: (node: KpiHierarchyNode) -> Unit,
        ) {
            if (!seen.add(node)) {
                return
            }

            node.hierarchyEdges.forEach { child ->
                depthFirstTraversal(node = child.to, seen = seen, action)
            }

            action(node)
        }
    }
}
