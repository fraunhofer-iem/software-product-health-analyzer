package de.fraunhofer.iem.kpiCalculator.core.hierarchy

internal data class KpiHierarchyEdge(
    val from: KpiHierarchyNode,
    val to: KpiHierarchyNode,
    val plannedWeight: Double,
    val actualWeight: Double = plannedWeight,
)
