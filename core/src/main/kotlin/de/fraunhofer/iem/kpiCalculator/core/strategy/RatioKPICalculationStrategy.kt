package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult

object RatioKPICalculationStrategy : KpiCalculationStrategy {
    /**
     * Returns smallerValue / biggerValue, regardless in which order the values are given.
     */
    override fun calculateKpi(
        successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        failed: List<Pair<KpiCalculationResult, Double>>,
        additionalWeight: Double,
        hasIncompleteResults: Boolean
    ): KpiCalculationResult {

        if (successScores.size != 2) {
            return KpiCalculationResult.Error(
                "Ratio calculation strategy called " +
                    "with ${successScores.size} valid elements, which is illegal."
            )
        }

        val biggerValue = if (successScores.first().first.score > successScores[1].first.score) {
            successScores.first().first.score
        } else {
            successScores.last().first.score
        }

        val smallerValue = if (successScores.first().first.score < successScores[1].first.score) {
            successScores.first().first.score
        } else {
            successScores[1].first.score
        }

        if (biggerValue == smallerValue && smallerValue == 0) {
            return KpiCalculationResult.Success(0)
        }

        return try {
            if (!hasIncompleteResults) {
                KpiCalculationResult.Success(
                    score = ((smallerValue.toDouble() / biggerValue.toDouble()) * 100).toInt()
                )
            } else {
                KpiCalculationResult.Incomplete(
                    score = ((smallerValue.toDouble() / biggerValue.toDouble()) * 100).toInt(),
                    reason = "Incomplete results.",
                    additionalWeights = 0.0
                )
            }
        } catch (e: Exception) {
            KpiCalculationResult.Error(e.message ?: e.toString())
        }
    }
}
