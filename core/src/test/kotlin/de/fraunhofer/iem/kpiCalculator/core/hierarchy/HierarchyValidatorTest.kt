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
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

fun randomNode(edges: List<KpiEdge> = listOf()): KpiNode {

    val rndIds = (0..<KpiId.entries.size).random()
    val rndStrategies = (0..<KpiStrategyId.entries.size).random()

    return KpiNode(
        kpiId = KpiId.entries[rndIds],
        kpiStrategyId = KpiStrategyId.entries[rndStrategies],
        edges = edges
    )
}

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

    @Test
    fun invalidRawValueHierarchy() {
        val invalidHierarchy = KpiHierarchy.create(
            rootNode = KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(
                    KpiEdge(
                        KpiNode(
                            kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                            kpiId = KpiId.NUMBER_OF_COMMITS,
                            edges = listOf(
                                KpiEdge(
                                    target = randomNode(),
                                    weight = 1.0
                                )
                            )
                        ),
                        weight = 1.0
                    )
                )
            )
        )

        assertEquals(false, HierarchyValidator.isValid(kpiHierarchy = invalidHierarchy, strict = true))
        assertEquals(false, HierarchyValidator.isValid(kpiHierarchy = invalidHierarchy, strict = false))

        val hierarchy = KpiHierarchy.create(
            rootNode = KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(
                    KpiEdge(
                        KpiNode(
                            kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                            kpiId = KpiId.NUMBER_OF_COMMITS,
                            edges = listOf(
                            ),
                        ),
                        weight = 1.0
                    )
                )
            )
        )

        assertEquals(true, HierarchyValidator.isValid(kpiHierarchy = hierarchy, strict = true))
        assertEquals(true, HierarchyValidator.isValid(kpiHierarchy = hierarchy, strict = false))
    }
}
