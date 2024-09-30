/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.core.hierarchy.KpiHierarchyNode
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RatioKPICalculationStrategyTest {

    @Test
    fun isValidEmpty() {
        val node =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges = listOf(),
            )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = false))
        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = true))

        val incorrectStrategy =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(),
            )

        assertEquals(
            true,
            RatioKPICalculationStrategy.isValid(node = incorrectStrategy, strict = false),
        )
        assertEquals(
            true,
            RatioKPICalculationStrategy.isValid(node = incorrectStrategy, strict = true),
        )
    }

    @Test
    fun calculateEmpty() {

        val calcRelaxed =
            RatioKPICalculationStrategy.calculateKpi(hierarchyEdges = listOf(), strict = false)
        val calcStrict =
            RatioKPICalculationStrategy.calculateKpi(hierarchyEdges = listOf(), strict = true)

        assertEquals(true, calcRelaxed is KpiCalculationResult.Empty)
        assertEquals(true, calcStrict is KpiCalculationResult.Empty)
    }

    @Test
    fun isValidCorrectChildren() {
        val nodeCorrectChildren =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.NUMBER_OF_COMMITS,
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
            )

        assertEquals(
            true,
            RatioKPICalculationStrategy.isValid(node = nodeCorrectChildren, strict = false),
        )
        assertEquals(
            true,
            RatioKPICalculationStrategy.isValid(node = nodeCorrectChildren, strict = true),
        )
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
            RatioKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = false,
            )
        val calcStrict =
            RatioKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = true,
            )

        assertEquals(true, calcRelaxed is KpiCalculationResult.Success)
        assertEquals(true, calcRelaxed is KpiCalculationResult.Success)

        assertEquals(75, (calcStrict as KpiCalculationResult.Success).score)
        assertEquals(75, (calcRelaxed as KpiCalculationResult.Success).score)
    }

    @Test
    fun isValidToManyChildren() {
        val nodeManyChildren =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.NUMBER_OF_COMMITS,
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
            )

        assertEquals(
            true,
            RatioKPICalculationStrategy.isValid(node = nodeManyChildren, strict = false),
        )
        assertEquals(
            false,
            RatioKPICalculationStrategy.isValid(node = nodeManyChildren, strict = true),
        )

        val root =
            KpiHierarchyNode.from(
                nodeManyChildren,
                listOf(RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 15)),
            )

        val relaxed = RatioKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = false)
        val strict = RatioKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = true)
        assertEquals(true, relaxed is KpiCalculationResult.Error)
        assertEquals(true, strict is KpiCalculationResult.Error)
    }

    @Test
    fun nestedError() {
        val nestedError =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.NUMBER_OF_COMMITS,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 0.5,
                        ),
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SECURITY,
                                    kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.NUMBER_OF_COMMITS,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = listOf(),
                                                    ),
                                                weight = 0.5,
                                            ),
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = listOf(),
                                                    ),
                                                weight = 0.5,
                                            ),
                                        ),
                                ),
                            weight = 0.5,
                        ),
                    ),
            )

        val root =
            KpiHierarchyNode.from(
                nestedError,
                listOf(
                    RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 0),
                    RawValueKpi(kind = KpiId.NUMBER_OF_SIGNED_COMMITS, score = -1),
                ),
            )

        KpiHierarchyNode.depthFirstTraversal(root) { it.calculateKpi() }

        assertEquals(true, root.result is KpiCalculationResult.Error)

        val relaxed = RatioKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = false)
        val strict = RatioKPICalculationStrategy.calculateKpi(root.hierarchyEdges, strict = true)

        assertEquals(true, relaxed is KpiCalculationResult.Error)
        assertEquals(true, strict is KpiCalculationResult.Error)
        assertEquals(
            "Ratio calculation strategy has elements without result",
            (strict as KpiCalculationResult.Error).reason,
        )
        assertEquals(
            "Ratio calculation strategy has elements without result",
            (relaxed as KpiCalculationResult.Error).reason,
        )
    }

    @Test
    fun isValidToFewChildren() {
        val nodeToFewChildren =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.NUMBER_OF_COMMITS,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 0.5,
                        )
                    ),
            )

        assertEquals(
            false,
            RatioKPICalculationStrategy.isValid(node = nodeToFewChildren, strict = false),
        )
        assertEquals(
            false,
            RatioKPICalculationStrategy.isValid(node = nodeToFewChildren, strict = true),
        )
    }
}
