package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode


internal object AggregationKPICalculationStrategy : KpiCalculationStrategy {

    override val kpiStrategyId: KpiStrategyId
        get() = KpiStrategyId.AGGREGATION_STRATEGY

    /**
     * This function calculates the aggregate sum of all given children.
     * If a child is empty it is removed from the calculation and its
     * corresponding edge weight is distributed evenly between the
     * remaining children.
     * The method returns the KPIs value as well as the updated
     * KPIHierarchyEdgeDtos with the actual used weight.
     */
    override fun calculateKpi(
        successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        failed: List<Pair<KpiCalculationResult, Double>>,
        additionalWeight: Double,
        hasIncompleteResults: Boolean
    ): KpiCalculationResult {

        val aggregation =
            if (successScores.isEmpty())
                0
            else
                successScores.sumOf {
                    (it.first).score * (it.second + additionalWeight)
                }.toInt()


        if (hasIncompleteResults) {
            return KpiCalculationResult.Incomplete(
                score = aggregation,
                reason = "There were ${failed.size} elements missing during aggregation.",
                additionalWeights = additionalWeight
            )
        }

        return KpiCalculationResult.Success(score = aggregation)
    }

    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}
