/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HierarchyValidatorTest {

    @Test
    fun emptyKpiHierarchy() {
        val hierarchy = KpiHierarchy.create(
            rootNode = KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = emptyList()
            )
        )

        assertEquals(false, HierarchyValidator.isValid(kpiHierarchy = hierarchy, strict = true))
        assertEquals(true, HierarchyValidator.isValid(kpiHierarchy = hierarchy, strict = false))
    }
}
