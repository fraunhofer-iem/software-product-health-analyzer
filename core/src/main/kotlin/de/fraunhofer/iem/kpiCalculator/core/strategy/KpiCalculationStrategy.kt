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

        val (successScores, failed, missingEdgeWeights, hasIncompleteResults) = processChildScores(childScores, strict)

        // distributes weights of incomplete edges evenly between existing edges
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
            hasIncompleteResults = hasIncompleteResults
        )
    }

    private data class SeparatedKpiResults(
        val successScores: List<Pair<KpiCalculationResult.Success, Double>>,
        val failedScores: List<Pair<KpiCalculationResult, Double>>,
        val missingEdgeWeights: Double,
        val hasIncompleteResults: Boolean
    )

    /**
     * Sorts the given childScores into separate lists for Success and Failed results.
     * The sorting of Incomplete results depends on the strict mode.
     *
     * In strict mode, we consider Incomplete results as failed, and they are not
     * considered in the KPI calculation.
     * In not strict mode, we cast Incomplete results to Success results and use
     * them for further calculation.
     *
     * @param childScores the KPI results and their planned edge weights.
     * @param strict mode which influences the separation.
     *
     * @return SeparatedKpiResults, which contain a List of success and failed scores,
     * missingEdgeWeights (the sum of all failed node's edge weights),
     * hasIncompleteResults (indicates whether the original childScores contained an
     * Incomplete result)
     */
    private fun processChildScores(
        childScores: Collection<Pair<KpiCalculationResult, Double>>,
        strict: Boolean
    ): SeparatedKpiResults {

        val successScores = mutableListOf<Pair<KpiCalculationResult.Success, Double>>()
        val failedScores = mutableListOf<Pair<KpiCalculationResult, Double>>()
        var missingEdgeWeight = 0.0
        var hasIncompleteResults = false

        childScores.forEach { childScore ->

            // we need to extract the pair into a single variable to enable
            // smart type casting in the following
            when (val calcResult = childScore.first) {
                is KpiCalculationResult.Success -> {
                    // type erasure can't handle parameterized types so smart
                    // casting won't work
                    @Suppress("UNCHECKED_CAST")
                    successScores.add(childScore as Pair<KpiCalculationResult.Success, Double>)
                }

                is KpiCalculationResult.Incomplete -> {
                    if (strict) {
                        failedScores.add(childScore)
                        missingEdgeWeight += childScore.second
                    } else {
                        successScores.add(
                            Pair(
                                KpiCalculationResult.Success(score = calcResult.score),
                                childScore.second
                            )
                        )
                    }
                    hasIncompleteResults = true
                }

                is KpiCalculationResult.Empty, is KpiCalculationResult.Error -> {
                    failedScores.add(childScore)
                    missingEdgeWeight += childScore.second
                    hasIncompleteResults = true
                }
            }
        }

        return SeparatedKpiResults(
            successScores = successScores,
            failedScores = failedScores,
            missingEdgeWeights = missingEdgeWeight,
            hasIncompleteResults = hasIncompleteResults
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
