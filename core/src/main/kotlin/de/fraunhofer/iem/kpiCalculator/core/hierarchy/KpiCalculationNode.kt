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
    val kind: KpiId,
    val calculationStrategy: KpiStrategyId,
    val parent: KpiCalculationNode?
) {

    private var score: KpiCalculationResult = KpiCalculationResult.Empty()
    fun setScore(score: Int) {
        this.score = KpiCalculationResult.Success(score)
    }


    private val _hierarchyEdges: MutableList<KpiHierarchyEdge> = mutableListOf()
    val hierarchyEdges: List<KpiHierarchyEdge>
        get() = _hierarchyEdges

    fun addChild(node: KpiCalculationNode, weight: Double) {
        _hierarchyEdges.add(KpiHierarchyEdge(to = node, from = this, weight = weight))
    }

    fun removeChild(node: KpiCalculationNode) {
        _hierarchyEdges.removeIf { it.to == node }
    }

    fun calculateKpi(): KpiCalculationResult {
        val strategyData = hierarchyEdges.map {
            Pair(it.to.score, it.weight)
        }
        score = when (calculationStrategy) {
            KpiStrategyId.RAW_VALUE_STRATEGY ->
                score

            KpiStrategyId.RATIO_STRATEGY ->
                RatioKPICalculationStrategy.calculateKpi(strategyData)

            KpiStrategyId.AGGREGATION_STRATEGY ->
                AggregationKPICalculationStrategy.calculateKpi(strategyData)

            KpiStrategyId.MAXIMUM_STRATEGY ->
                MaximumKPICalculationStrategy.calculateKpi(strategyData)
        }

        return score
    }

    companion object {
        fun to(node: KpiCalculationNode): KpiResultNode {
            return KpiResultNode(
                kpiId = node.kind,
                strategyType = node.calculationStrategy,
                kpiResult = node.score,
                children = node.hierarchyEdges.map {
                    KpiResultEdge(
                        target = to(it.to),
                        weight = it.weight
                    )
                }
            )
        }

        fun from(node: KpiNode): KpiCalculationNode {
            return from(node, parent = null)
        }

        private fun from(node: KpiNode, parent: KpiCalculationNode? = null): KpiCalculationNode {

            val calcNode =
                KpiCalculationNode(kind = node.kpiId, parent = parent, calculationStrategy = node.strategyType)
            val children = node.children.map { child ->
                KpiHierarchyEdge(
                    to = from(child.target, calcNode),
                    from = calcNode,
                    weight = child.weight
                )
            }
            calcNode._hierarchyEdges.addAll(children)

            return calcNode
        }
    }
}
