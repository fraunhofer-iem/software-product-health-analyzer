/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.core.hierarchy.KpiHierarchyEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal object MaximumKPICalculationStrategy : BaseKpiCalculationStrategy() {

    override val kpiStrategyId: KpiStrategyId
        get() = KpiStrategyId.MAXIMUM_STRATEGY

    override fun internalCalculateKpi(edges: Collection<KpiHierarchyEdge>): KpiCalculationResult {

        val max = if (edges.isEmpty()) 0 else edges.maxOf { it.to.score }

        return KpiCalculationResult.Success(score = max)
    }

    /** There is no validity requirement for this strategy. */
    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {

        if (node.edges.size == 1) {
            logger.warn {
                "Maximum KPI calculation strategy for node $node is planned " +
                    "for a single child."
            }
        }

        return true
    }
}
