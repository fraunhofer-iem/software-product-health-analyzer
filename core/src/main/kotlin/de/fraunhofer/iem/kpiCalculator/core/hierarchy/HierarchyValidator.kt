/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.core.strategy.AggregationKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.MaximumKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.core.strategy.RatioKPICalculationStrategy
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

object HierarchyValidator {

    /**
     * Validates the semantic structure of the given KpiHierarchy.
     *
     * Checks edge weight sums (must always be 1.0).
     *
     * Uses KpiCalculationStrategy isValid method on each node.
     *
     * @param kpiHierarchy hierarchy to validate.
     * @param strict validation mode. Some errors in strict may only be warnings in relaxed mode.
     *
     * @return if the hierarchy is valid.
     */
    fun isValid(kpiHierarchy: KpiHierarchy, strict: Boolean = false): Boolean {
        // TODO: check the schema version and build a schema specific validation if necessary
        logger.info { "Started KPI hierarchy validation with strict mode $strict." }
        val root = kpiHierarchy.rootNode

        // handle empty hierarchies
        if (root.edges.isEmpty()) {
            if (strict) {
                return false
            } else {
                logger.warn { "Provided KPI hierarchy is empty." }
            }
        }


        return validateStrategies(root, strict)
    }

    private fun validateStrategies(node: KpiNode, strict: Boolean): Boolean {

        if (node.edges.isNotEmpty()) {
            val edgeWeightSum = node.edges.sumOf { it.weight }
            if (edgeWeightSum != 1.0) {
                logger.error {
                    "Incorrect edge weights. Should sum up to 1.0 " +
                            "actual sum of weights $edgeWeightSum."
                }
                return false
            }
        }

        val isValid = when (node.kpiStrategyId) {
            KpiStrategyId.RAW_VALUE_STRATEGY -> {
                if (node.edges.isNotEmpty()) {
                    logger.error { "Raw Value nodes must not have children as they expect only tool results." }
                    false
                } else {
                    true
                }
            }

            KpiStrategyId.AGGREGATION_STRATEGY -> AggregationKPICalculationStrategy.isValid(node, strict)
            KpiStrategyId.MAXIMUM_STRATEGY -> MaximumKPICalculationStrategy.isValid(node, strict)
            KpiStrategyId.RATIO_STRATEGY -> RatioKPICalculationStrategy.isValid(node, strict)
        }

        if (!isValid) {
            return false
        }

        node.edges.forEach { edge ->
            val isValidEdge = validateStrategies(edge.target, strict)
            if (!isValidEdge) {
                return@validateStrategies false
            }
        }

        return true
    }
}
