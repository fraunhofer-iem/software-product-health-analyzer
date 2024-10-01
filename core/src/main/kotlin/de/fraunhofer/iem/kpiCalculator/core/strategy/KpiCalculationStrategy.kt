/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.core.hierarchy.KpiHierarchyEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode

internal interface KpiCalculationStrategy {
    /**
     * Calculates a KpiCalculationResult by applying the KpiCalculation strategy on the given child
     * scores.
     *
     * @param childScores List of KpiCalculationResults and their corresponding weights.
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
            hierarchyEdges.forEach { it.actualWeight = 0.0 }
            return KpiCalculationResult.Empty()
        }

        updateEdgeWeights(edges = hierarchyEdges, strict)

        val result = internalCalculateKpi(hierarchyEdges)

        if (
            hierarchyEdges.any { it.to.result !is KpiCalculationResult.Success } &&
                result is KpiCalculationResult.Success
        ) {
            return KpiCalculationResult.Incomplete(
                score = result.score,
                reason = "Incomplete results.",
            )
        }

        return result
    }

    /**
     * Sorts the given childScores into separate lists for Success and Failed results. The sorting
     * of Incomplete results depends on the strict mode.
     *
     * In strict mode, we consider Incomplete results as failed, and they are not considered in the
     * KPI calculation. In not strict mode, we cast Incomplete results to Success results and use
     * them for further calculation.
     *
     * @param childScores the KPI results and their planned edge weights.
     * @param strict mode which influences the separation.
     * @return SeparatedKpiResults, which contain a List of success and failed scores,
     *   missingEdgeWeights (the sum of all failed node's edge weights), hasIncompleteResults
     *   (indicates whether the original childScores contained an Incomplete result)
     */
    private fun updateEdgeWeights(edges: Collection<KpiHierarchyEdge>, strict: Boolean) {
        var missingEdgeWeight = 0.0
        var counterIncompleteEdges = 0

        edges.forEach { edge ->
            if (
                edge.to.hasNoResult() || strict && edge.to.result is KpiCalculationResult.Incomplete
            ) {
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
