package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode

internal object RatioKPICalculationStrategy : BaseKpiCalculationStrategy() {

    override val kpiStrategyId: KpiStrategyId
        get() = KpiStrategyId.RATIO_STRATEGY

    /**
     * Returns smallerValue / biggerValue, regardless in which order the values are given.
     */
    override fun internalCalculateKpi(
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

    /**
     * Validates whether the given KPI node is a valid ratio calculation node.
     * If the given node's strategy is not RATIO_STRATEGY, we return true.
     *
     * If the number of children is not two a warning
     * is generated, regardless of the used mode.
     *
     * @param node KPI node to validate.
     * @param strict validation mode, true implies that a valid node must contain exactly two children.
     * False implies, that a valid node must contain two or more children.
     * @return if the given node is valid.
     */
    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {

        if (strict) {
            return node.edges.size == 2
        }

        if (node.edges.size > 2) {
            // TODO: log a warning here
            return true
        }

        return node.edges.size > 1
    }
}
