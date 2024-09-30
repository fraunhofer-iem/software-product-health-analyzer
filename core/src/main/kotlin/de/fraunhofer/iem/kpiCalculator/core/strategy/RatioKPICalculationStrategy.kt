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
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal object RatioKPICalculationStrategy : BaseKpiCalculationStrategy() {

    override val kpiStrategyId: KpiStrategyId
        get() = KpiStrategyId.RATIO_STRATEGY

    /** Returns smallerValue / biggerValue, regardless in which order the values are given. */
    override fun internalCalculateKpi(edges: Collection<KpiHierarchyEdge>): KpiCalculationResult {

        if (edges.size != 2) {
            return KpiCalculationResult.Error(
                "Ratio calculation strategy called " +
                    "with ${edges.size} valid elements, which is illegal."
            )
        }

        if (edges.any { it.to.hasNoResult() }) {
            return KpiCalculationResult.Error(
                reason = "Ratio calculation strategy has elements without result"
            )
        }

        val biggerValue: Int
        val smallerValue: Int

        if (edges.first().to.score > edges.last().to.score) {
            biggerValue = edges.first().to.score
            smallerValue = edges.last().to.score
        } else {

            biggerValue = edges.last().to.score
            smallerValue = edges.first().to.score
        }

        val ratio =
            try {
                if (biggerValue != 0) {
                    smallerValue.toDouble() / biggerValue.toDouble()
                } else {
                    // NB: for whatever reason the statement above results in -Infinity
                    // instead of an ArithmeticException when  dividing by 0.
                    throw ArithmeticException("Tried division by 0")
                }
            } catch (e: Exception) {
                logger.error { "Error " }
                return KpiCalculationResult.Error(e.message ?: e.toString())
            }
        return KpiCalculationResult.Success(score = (ratio * 100).toInt())
    }

    /**
     * Validates whether the given KPI node is a valid ratio calculation node. If the given node's
     * strategy is not RATIO_STRATEGY, we return true.
     *
     * If the number of children is not two a warning is generated, regardless of the used mode.
     *
     * @param node KPI node to validate.
     * @param strict validation mode, true implies that a valid node must contain exactly two
     *   children. False implies, that a valid node must contain two or more children.
     * @return if the given node is valid.
     */
    override fun internalIsValid(node: KpiNode, strict: Boolean): Boolean {

        if (strict) {
            return node.edges.size == 2
        }

        if (node.edges.size > 2) {
            logger.warn {
                "Ratio KPI calculation strategy for node $node has more than two children." +
                    "This is allowed in relaxed mode, however it is not defined, which" +
                    "children are selected for calculation. This can lead to" +
                    "ambiguous results."
            }

            return true
        }

        return node.edges.size > 1
    }
}
