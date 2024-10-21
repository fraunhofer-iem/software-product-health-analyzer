/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.core.strategy

import de.fraunhofer.iem.spha.core.hierarchy.KpiHierarchyNode
import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import de.fraunhofer.iem.spha.model.kpi.RawValueKpi
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiNode
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertEquals

class AggregationKpiCalculationStrategyTest {

    @Test
    fun calculateEmpty() {

        val calcRelaxed =
            AggregationKPICalculationStrategy.calculateKpi(
                hierarchyEdges = listOf(),
                strict = false,
            )
        val calcStrict =
            AggregationKPICalculationStrategy.calculateKpi(hierarchyEdges = listOf(), strict = true)

        assertEquals(true, calcRelaxed is KpiCalculationResult.Empty)
        assertEquals(true, calcStrict is KpiCalculationResult.Empty)
    }

    @Test
    fun calculateCorrect() {
        val root =
            KpiHierarchyNode.from(
                KpiNode(
                    kpiId = KpiId.ROOT,
                    kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                    edges =
                        listOf(
                            KpiEdge(
                                target =
                                    KpiNode(
                                        kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
                                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                        edges = listOf(),
                                    ),
                                weight = 0.5,
                            ),
                            KpiEdge(
                                target =
                                    KpiNode(
                                        kpiId = KpiId.NUMBER_OF_COMMITS,
                                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                        edges = listOf(),
                                    ),
                                weight = 0.5,
                            ),
                        ),
                ),
                listOf(
                    RawValueKpi(kind = KpiId.NUMBER_OF_SIGNED_COMMITS, score = 15),
                    RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 20),
                ),
            )

        val calcRelaxed =
            AggregationKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = false)
        val calcStrict =
            AggregationKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = true)

        assert(calcRelaxed is KpiCalculationResult.Success)
        assert(calcStrict is KpiCalculationResult.Success)
        assertEquals(
            ((20 * 0.5) + (15 * 0.5)).toInt(),
            (calcStrict as KpiCalculationResult.Success).score,
        )
        assertEquals(
            ((20 * 0.5) + (15 * 0.5)).toInt(),
            (calcRelaxed as KpiCalculationResult.Success).score,
        )
    }
}
