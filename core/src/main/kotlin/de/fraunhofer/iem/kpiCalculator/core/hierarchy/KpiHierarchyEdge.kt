package de.fraunhofer.iem.kpiCalculator.core.hierarchy

internal data class KpiHierarchyEdge(
    val from: KpiCalculationNode,
    val to: KpiCalculationNode,
    val plannedWeight: Double,
    val actualWeight: Double = plannedWeight,
)
