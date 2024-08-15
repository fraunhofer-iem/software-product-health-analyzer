/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

object HierarchyValidator {
    //TODO: return type will most likely be changed to a complex type comparably to KpiCalculationResult
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

        return true
    }
}
