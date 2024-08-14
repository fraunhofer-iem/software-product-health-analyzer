package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode

interface KpiCalculationStrategy {
    /**
     * Calculates a KpiCalculationResult by applying the KpiCalculation strategy on
     * the given child scores.
     *
     * @param childScores List of KpiCalculationResults and their corresponding weights.
     * @param strict calculation mode, true implies that the strategy will only take
     * childScores of type Success into account. False implies that the calculation
     * is also performed on Incomplete childScores.
     *
     * @return KpiCalculation result created by applying the KpiCalculationStrategy
     * on the given childScores.
     */
    fun calculateKpi(
        childScores: Collection<Pair<KpiCalculationResult, Double>>,
        strict: Boolean = false
    ): KpiCalculationResult

    /**
     * Validates whether the given KPI node's structure is valid for its strategy.
     * If the given node's strategy does not match the kpiStrategyId, we return true.
     * If the given node's edges are empty, we return true.
     *
     * @param node KPI node to validate.
     * @param strict validation mode, true implies that a valid node must exactly match our expectations.
     * False implies, that a node is considered valid if it can be used for further calculation, but it
     * might result in inconsistent results. E.g., we expect two children but receive three, not strict
     * mode allows this, but it is not well-defined which of the three nodes will be used during
     * KPI calculation.
     *
     * @return if the given node is valid.
     */
    fun isValid(node: KpiNode, strict: Boolean = false): Boolean
}

internal abstract class BaseKpiCalculationStrategy : KpiCalculationStrategy {

    abstract val kpiStrategyId: KpiStrategyId

    override fun calculateKpi(
        childScores: Collection<Pair<KpiCalculationResult, Double>>,
        strict: Boolean
    ): KpiCalculationResult {

        if (childScores.isEmpty()) {
            return KpiCalculationResult.Empty()
        }

        val incompleteResults = if (!strict) {
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
                return@filter (it.first is KpiCalculationResult.Error) ||
                        (it.first is KpiCalculationResult.Empty) ||
                        (strict && it.first is KpiCalculationResult.Incomplete)
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

        return internalCalculateKpi(
            successScores = successScores,
            failed = failed,
            additionalWeight = additionalWeight,
            hasIncompleteResults = incompleteResults.isNotEmpty() || failed.isNotEmpty()
        )

    }

    protected abstract fun internalCalculateKpi(
        successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        failed: List<Pair<KpiCalculationResult, Double>>,
        additionalWeight: Double,
        hasIncompleteResults: Boolean
    ): KpiCalculationResult

    override fun isValid(node: KpiNode, strict: Boolean): Boolean {
        if (node.kpiStrategyId != kpiStrategyId) {
            return true
        }

        if (node.edges.isEmpty()) {
            return true
        }

        return internalIsValid(node, strict)
    }

    protected abstract fun internalIsValid(node: KpiNode, strict: Boolean): Boolean
}
