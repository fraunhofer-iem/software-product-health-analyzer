package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.core.strategy.AggregationKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.MaximumKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.RatioKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiResultNode


internal class KpiCalculationNode(
    val kpiId: KpiId,
    val calculationStrategy: KpiStrategyId,
    val parent: KpiCalculationNode?
) {

    private var result: KpiCalculationResult = KpiCalculationResult.Empty()
    fun setResult(result: Int) {
        this.result = KpiCalculationResult.Success(result)
    }


    private val _hierarchyEdges: MutableList<KpiHierarchyEdge> = mutableListOf()
    val hierarchyEdges: List<KpiHierarchyEdge>
        get() = _hierarchyEdges

    fun addChild(node: KpiCalculationNode, weight: Double) {
        _hierarchyEdges.add(KpiHierarchyEdge(to = node, from = this, plannedWeight = weight))
    }

    fun removeChild(node: KpiCalculationNode) {
        _hierarchyEdges.removeIf { it.to == node }
    }

    fun getWeight(node: KpiCalculationNode): Double? {
        return hierarchyEdges.find { it.to == node }?.actualWeight
    }

    fun calculateKpi(): KpiCalculationResult {
        val strategyData = hierarchyEdges.map {
            Pair(it.to.result, it.actualWeight)
        }
        result = when (calculationStrategy) {
            KpiStrategyId.RAW_VALUE_STRATEGY ->
                result

            KpiStrategyId.RATIO_STRATEGY ->
                RatioKPICalculationStrategy.calculateKpi(strategyData)

            KpiStrategyId.AGGREGATION_STRATEGY ->
                AggregationKPICalculationStrategy.calculateKpi(strategyData)

            KpiStrategyId.MAXIMUM_STRATEGY ->
                MaximumKPICalculationStrategy.calculateKpi(strategyData)
        }
        updateEdgeWeights(result)
        return result
    }

    private fun updateEdgeWeights(result: KpiCalculationResult) {
        val updatedEdges = hierarchyEdges.map { edge ->

            if (result is KpiCalculationResult.Success) {
                return@map edge
            }

            val targetResult = edge.to.result

            if (result is KpiCalculationResult.Incomplete
                && (targetResult !is KpiCalculationResult.Empty
                    && targetResult !is KpiCalculationResult.Error)
            ) {
                return@map KpiHierarchyEdge(
                    from = this,
                    to = edge.to,
                    plannedWeight = edge.plannedWeight,
                    actualWeight = edge.plannedWeight + result.additionalWeights
                )
            }

            return@map KpiHierarchyEdge(
                from = this,
                to = edge.to,
                plannedWeight = edge.plannedWeight,
                actualWeight = 0.0
            )
        }

        _hierarchyEdges.clear()
        _hierarchyEdges.addAll(updatedEdges)
    }

    companion object {
        fun to(node: KpiCalculationNode): KpiResultNode {
            return KpiResultNode(
                kpiId = node.kpiId,
                strategyType = node.calculationStrategy,
                kpiResult = node.result,
                children = node.hierarchyEdges.map {
                    KpiResultEdge(
                        target = to(it.to),
                        plannedWeight = it.plannedWeight,
                        actualWeight = it.actualWeight
                    )
                }
            )
        }

        fun from(node: KpiNode): KpiCalculationNode {
            return from(node, parent = null)
        }

        private fun from(node: KpiNode, parent: KpiCalculationNode? = null): KpiCalculationNode {

            val calcNode =
                KpiCalculationNode(kpiId = node.kpiId, parent = parent, calculationStrategy = node.strategyType)
            val children = node.children.map { child ->
                KpiHierarchyEdge(
                    to = from(child.target, calcNode),
                    from = calcNode,
                    plannedWeight = child.weight
                )
            }
            calcNode._hierarchyEdges.addAll(children)

            return calcNode
        }
    }
}
