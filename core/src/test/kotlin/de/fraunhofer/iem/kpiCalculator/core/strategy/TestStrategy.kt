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

internal class TestStrategy(val callback: (edges: Collection<KpiHierarchyEdge>) -> Unit) :
    BaseKpiCalculationStrategy() {
    override val kpiStrategyId: KpiStrategyId
        get() = TODO("Not yet implemented")

    override fun internalCalculateKpi(edges: Collection<KpiHierarchyEdge>): KpiCalculationResult {
        callback(edges)
        return KpiCalculationResult.Empty()
    }

    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}
