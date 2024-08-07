package de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import kotlinx.serialization.Serializable

val SCHEMA_VERSIONS: Array<String> = arrayOf(
    "1.0.0"
).sortedArray()

//TODO: add Hierarchy Validator
@Serializable
data class KpiHierarchy private constructor(val rootNode: KpiNode, val schemaVersion: String) {
    companion object {
        fun create(rootNode: KpiNode) = KpiHierarchy(rootNode, SCHEMA_VERSIONS.last())
    }
}

@Serializable
data class KpiNode(val kpiId: KpiId, val strategyType: KpiStrategyId, val children: List<KpiEdge>)

@Serializable
data class KpiEdge(val target: KpiNode, val weight: Double)

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
data class KpiResultEdge(val target: KpiResultNode, val weight: Double)

@Serializable
sealed class KpiCalculationResult {
    data class Success(val score: Int) : KpiCalculationResult()
    data class Error(val reason: String) : KpiCalculationResult()
    data class Incomplete(val score: Int, val additionalWeights: Double, val reason: String) : KpiCalculationResult()
    data class Empty(val reason: String = "This KPI is empty") : KpiCalculationResult()
}
