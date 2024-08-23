/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import kotlinx.serialization.Serializable

@ConsistentCopyVisibility
@Serializable
data class KpiResultHierarchy private constructor(val rootNode: KpiResultNode, val schemaVersion: String) {
    companion object {
        fun create(rootNode: KpiResultNode) = KpiResultHierarchy(rootNode, SCHEMA_VERSIONS.last())
    }
}

@Serializable
data class KpiResultNode(
    val kpiId: KpiId,
    val kpiResult: KpiCalculationResult,
    val strategyType: KpiStrategyId,
    val children: List<KpiResultEdge>
)

@Serializable
data class KpiResultEdge(val target: KpiResultNode, val plannedWeight: Double, val actualWeight: Double)

@Serializable
sealed class KpiCalculationResult {
    data class Success(val score: Int) : KpiCalculationResult()
    data class Error(val reason: String) : KpiCalculationResult()
    data class Incomplete(val score: Int, val additionalWeights: Double, val reason: String) : KpiCalculationResult()
    data class Empty(val reason: String = "This KPI is empty") : KpiCalculationResult()
}
