/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.core.strategy

import de.fraunhofer.iem.spha.core.hierarchy.KpiHierarchyEdge
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiNode

internal fun getKpiCalculationStrategy(strategyId: KpiStrategyId): KpiCalculationStrategy {
    return when (strategyId) {
        KpiStrategyId.RAW_VALUE_STRATEGY -> RawValueKpiCalculationStrategy
        KpiStrategyId.RATIO_STRATEGY -> RatioKPICalculationStrategy

        KpiStrategyId.AGGREGATION_STRATEGY -> AggregationKPICalculationStrategy

        KpiStrategyId.MAXIMUM_STRATEGY -> MaximumKPICalculationStrategy

        KpiStrategyId.WEIGHTED_MAXIMUM_STRATEGY -> MaximumKPICalculationStrategy
    }
}

internal interface KpiCalculationStrategy {
    /**
     * Calculates a KpiCalculationResult by applying the KpiCalculation strategy on the given child
     * scores.
     *
     * @param hierarchyEdges based on whose results this result is calculated.
     * @param strict calculation mode, true implies that the strategy will only take childScores of
     *   type Success into account. False implies that the calculation is also performed on
     *   Incomplete childScores.
     * @return KpiCalculation result created by applying the KpiCalculationStrategy on the given
     *   childScores.
     */
    fun calculateKpi(
        hierarchyEdges: Collection<KpiHierarchyEdge>,
        strict: Boolean = false,
    ): KpiCalculationResult

    /**
     * Validates whether the given KPI node's structure is valid for its strategy. If the given
     * node's strategy does not match the kpiStrategyId, we return true. If the given node's edges
     * are empty, we return true.
     *
     * @param node KPI node to validate.
     * @param strict validation mode, true implies that a valid node must exactly match our
     *   expectations. False implies, that a node is considered valid if it can be used for further
     *   calculation, but it might result in inconsistent results. E.g., we expect two children but
     *   receive three, not strict mode allows this, but it is not well-defined which of the three
     *   nodes will be used during KPI calculation.
     * @return if the given node is valid.
     */
    fun isValid(node: KpiNode, strict: Boolean = false): Boolean
}

internal abstract class BaseKpiCalculationStrategy : KpiCalculationStrategy {

    abstract val kpiStrategyId: KpiStrategyId

    final override fun calculateKpi(
        hierarchyEdges: Collection<KpiHierarchyEdge>,
        strict: Boolean,
    ): KpiCalculationResult {

        if (
            (strict && hierarchyEdges.none { it.to.result is KpiCalculationResult.Success }) ||
                hierarchyEdges.none { !it.to.hasNoResult() } ||
                hierarchyEdges.isEmpty()
        ) {
            // There are no valid results to use, thus we set every actualWeight to 0 and return
            // the empty result
            hierarchyEdges.forEach { it.actualWeight = 0.0 }
            return KpiCalculationResult.Empty()
        }

        updateEdgeWeights(edges = hierarchyEdges, strict)

        val result = internalCalculateKpi(hierarchyEdges)

        if (
            hierarchyEdges.any { it.to.result !is KpiCalculationResult.Success } &&
                result is KpiCalculationResult.Success
        ) {
            // Even if the current calculation returns success, we repackage this result into an
            // Incomplete result, if any of the edges had no result / an incomplete results to
            // propagate
            // this information to the top. Every result depending on incomplete information is also
            // incomplete.
            return KpiCalculationResult.Incomplete(
                score = result.score,
                reason = "Incomplete results.",
            )
        }

        return result
    }

    /**
     * Performs an inplace update of every edge's `actualWeight` value, depending on the results of
     * all given edges. Edges with no result, or, in strict mode, an incomplete result, get a weight
     * of 0.0, as they are ignored for further calculation.Their `plannedWeight` is distributed
     * evenly between all edges with valid results attached to them.
     *
     * @param edges whose weights are updated by this function.
     * @param strict mode which defines if incomplete edges should be used or not.
     */
    private fun updateEdgeWeights(edges: Collection<KpiHierarchyEdge>, strict: Boolean) {
        var missingEdgeWeight = 0.0
        var counterIncompleteEdges = 0

        edges.forEach { edge ->
            if (edge.to.hasNoResult() || (strict && edge.to.hasIncompleteResult())) {
                missingEdgeWeight += edge.plannedWeight
                edge.actualWeight = 0.0
                counterIncompleteEdges++
            }
        }

        if (missingEdgeWeight != 0.0 && edges.size - counterIncompleteEdges > 0) {
            missingEdgeWeight /= edges.size - counterIncompleteEdges
        }

        edges
            .filter { !it.to.hasNoResult() }
            .forEach { edge -> edge.actualWeight = edge.plannedWeight + missingEdgeWeight }
    }

    protected abstract fun internalCalculateKpi(
        edges: Collection<KpiHierarchyEdge>
    ): KpiCalculationResult

    final override fun isValid(node: KpiNode, strict: Boolean): Boolean {
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
