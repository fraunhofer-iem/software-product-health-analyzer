package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult

interface KpiCalculationStrategy {

    fun calculateKpi(
        childScores: List<Pair<KpiCalculationResult, Double>>,
        considerIncomplete: Boolean = true
    ): KpiCalculationResult {

        if (childScores.isEmpty()) {
            return KpiCalculationResult.Empty()
        }

        val incompleteResults = if (considerIncomplete) {
            childScores.mapNotNull {
                val res = it.first
                if (res is KpiCalculationResult.Incomplete) {
                    Pair(KpiCalculationResult.Success(score = res.score), it.second)
                } else {
                    null
                }
            }
        } else {
            emptyList()
        }

        @Suppress("UNCHECKED_CAST")
        val successScores = listOf(
            childScores.filter { it.first is KpiCalculationResult.Success },
            incompleteResults
        ).flatten() as List<Pair<KpiCalculationResult.Success, Double>>

        val failed = childScores
            .filter {
                it.first is KpiCalculationResult.Error
                    || (it.first is KpiCalculationResult.Empty)
            }

        val missingEdgeWeights = failed.sumOf { it.second }

        val additionalWeight =
            if (missingEdgeWeights == 0.0)
                0.0
            else
                missingEdgeWeights / successScores.size

        if (successScores.isEmpty()) {
            return KpiCalculationResult.Incomplete(
                score = 0,
                additionalWeights = additionalWeight,
                reason = "No valid success scores."
            )
        }

        return calculateKpi(
            successScores = successScores,
            failed = failed,
            additionalWeight = additionalWeight,
            hasIncompleteResults = incompleteResults.isNotEmpty() || failed.isNotEmpty()
        )

    }

    fun calculateKpi(
        successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        failed: List<Pair<KpiCalculationResult, Double>>,
        additionalWeight: Double,
        hasIncompleteResults: Boolean
    ): KpiCalculationResult
}
