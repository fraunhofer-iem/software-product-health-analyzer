package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult

internal object MaximumKPICalculationStrategy : KpiCalculationStrategy {
    override fun calculateKpi(
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
}
