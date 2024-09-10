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
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode

internal fun randomKpiHierarchyNode(parent: KpiHierarchyNode? = null): KpiHierarchyNode {

    val rndIds = (0..<KpiId.entries.size).random()
    val rndStrategies = (0..<KpiStrategyId.entries.size).random()

    return KpiHierarchyNode(
        kpiId = KpiId.entries[rndIds],
        kpiStrategyId = KpiStrategyId.entries[rndStrategies],
        parent = parent,
    )
}

fun randomNode(edges: List<KpiEdge> = listOf()): KpiNode {

    val rndIds = (0..<KpiId.entries.size).random()
    val rndStrategies = (0..<KpiStrategyId.entries.size).random()

    return KpiNode(
        kpiId = KpiId.entries[rndIds],
        kpiStrategyId = KpiStrategyId.entries[rndStrategies],
        edges = edges,
    )
}
