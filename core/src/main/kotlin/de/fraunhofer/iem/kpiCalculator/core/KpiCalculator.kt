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
import de.fraunhofer.iem.kpiCalculator.core.hierarchy.KpiHierarchyNode.Companion.depthFirstTraversal
import de.fraunhofer.iem.kpiCalculator.core.strategy.AggregationKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.MaximumKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.RatioKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultHierarchy

object KpiCalculator {
    // XXX: Setup Logger

    fun calculateKpis(
        hierarchy: KpiHierarchy,
        rawValueKpis: List<RawValueKpi>,
        strict: Boolean = false,
    ): KpiResultHierarchy {
        val root = KpiHierarchyNode.from(hierarchy.rootNode, rawValueKpis)

        depthFirstTraversal(root) { it.result = calculateKpi(it, strict) }

        return KpiResultHierarchy.create(KpiHierarchyNode.to(root))
    }

    internal fun calculateKpi(
        node: KpiHierarchyNode,
        strict: Boolean = false,
    ): KpiCalculationResult {
        val result =
            when (node.kpiStrategyId) {
                KpiStrategyId.RAW_VALUE_STRATEGY -> node.result

                KpiStrategyId.RATIO_STRATEGY ->
                    RatioKPICalculationStrategy.calculateKpi(node.hierarchyEdges, strict)

                KpiStrategyId.AGGREGATION_STRATEGY ->
                    AggregationKPICalculationStrategy.calculateKpi(node.hierarchyEdges, strict)

                KpiStrategyId.MAXIMUM_STRATEGY ->
                    MaximumKPICalculationStrategy.calculateKpi(node.hierarchyEdges, strict)
            }
        return result
    }
}
