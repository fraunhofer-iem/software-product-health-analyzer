package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode

internal object MaximumKPICalculationStrategy : BaseKpiCalculationStrategy() {

    override val kpiStrategyId: KpiStrategyId
        get() = KpiStrategyId.MAXIMUM_STRATEGY

    override fun internalCalculateKpi(
        successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        failed: List<Pair<KpiCalculationResult, Double>>,
        additionalWeight: Double,
        hasIncompleteResults: Boolean
    ): KpiCalculationResult {

        val max =
            if (successScores.isEmpty())
                0
            else
                successScores.maxOf { it.first.score }

        if (hasIncompleteResults) {
            return KpiCalculationResult.Incomplete(
                score = max,
                reason = "Missing ${failed.size} elements.",
                additionalWeights = additionalWeight
            )
        }

        return KpiCalculationResult.Success(score = max)
    }

    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}
