/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.core.strategy

import de.fraunhofer.iem.spha.core.KpiCalculator
import de.fraunhofer.iem.spha.core.hierarchy.KpiHierarchyEdge
import de.fraunhofer.iem.spha.core.hierarchy.KpiHierarchyNode
import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import de.fraunhofer.iem.spha.model.kpi.RawValueKpi
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiNode
import kotlin.random.Random
import kotlin.test.fail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal fun getNodeWithErrorResult(plannedWeight: Double): KpiHierarchyNode {
    val node =
        KpiNode(
            kpiId = KpiId.NUMBER_OF_COMMITS,
            kpiStrategyId = KpiStrategyId.WEIGHTED_RATIO_STRATEGY,
            edges =
                listOf(
                    KpiEdge(
                        KpiNode(
                            kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
                            kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                            edges = listOf(),
                        ),
                        weight = plannedWeight,
                    )
                ),
        )

    val hierarchyNode =
        KpiHierarchyNode.from(node, listOf(RawValueKpi(KpiId.NUMBER_OF_SIGNED_COMMITS, score = 20)))
    KpiCalculator.calculateKpi(hierarchyNode)
    return hierarchyNode
}

internal fun getNodeIncompleteResult(plannedWeight: Double): KpiHierarchyNode {

    val node =
        KpiNode(
            kpiId = KpiId.SECRETS,
            kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
            edges =
                listOf(
                    KpiEdge(
                        KpiNode(
                            kpiId = KpiId.NUMBER_OF_COMMITS,
                            kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                            edges = listOf(),
                        ),
                        weight = plannedWeight,
                    ),
                    KpiEdge(
                        KpiNode(
                            kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
                            kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                            edges = listOf(),
                        ),
                        weight = plannedWeight,
                    ),
                ),
        )

    val hierarchyNode =
        KpiHierarchyNode.from(node, listOf(RawValueKpi(KpiId.NUMBER_OF_SIGNED_COMMITS, score = 20)))
    return hierarchyNode
}

class AbstractKpiCalculationTest {

    @Test
    fun calculateKpiEmptyChildren() {
        fun callback(edges: Collection<KpiHierarchyEdge>) {
            fail()
        }

        val testStrategy = TestStrategy(callback = ::callback)

        val emptyRelaxed = testStrategy.calculateKpi(hierarchyEdges = emptyList(), strict = false)

        assert(emptyRelaxed is KpiCalculationResult.Empty)

        val emptyStrict = testStrategy.calculateKpi(hierarchyEdges = emptyList(), strict = true)

        assert(emptyStrict is KpiCalculationResult.Empty)
    }

    @Test
    fun calculateKpiSuccess() {

        val nodeCorrectChildren =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.WEIGHTED_RATIO_STRATEGY,
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

        val root =
            KpiHierarchyNode.from(
                nodeCorrectChildren,
                listOf(RawValueKpi(KpiId.NUMBER_OF_COMMITS, 20)),
            )

        fun callback(edges: Collection<KpiHierarchyEdge>) {
            assertEquals(2, edges.size)
            assertEquals(0.5, edges.first().actualWeight)
            assertEquals(0.5, edges.first().actualWeight)
            assertEquals(0.5, edges.last().plannedWeight)
            assertEquals(0.5, edges.last().plannedWeight)
        }

        val testStrategy = TestStrategy(callback = ::callback)
        testStrategy.calculateKpi(root.hierarchyEdges)
    }

    @Test
    fun calculateErrorKpisNewEdgeWeights() {
        val errorNode = getNodeWithErrorResult(plannedWeight = 0.5)

        val edges = listOf(KpiHierarchyEdge(to = errorNode, plannedWeight = 0.5))

        fun callback(edges: Collection<KpiHierarchyEdge>) {
            fail()
        }

        val testStrategy = TestStrategy(callback = ::callback)
        testStrategy.calculateKpi(edges)
        assertEquals(0.5, edges.first().plannedWeight)
        assertEquals(0.0, edges.first().actualWeight)
    }

    @Test
    fun calculateKpisIncomplete() {

        val incompleteNode = getNodeIncompleteResult(plannedWeight = 0.5)
        val incompleteResult = KpiCalculator.calculateKpi(incompleteNode, false)
        assert(incompleteResult is KpiCalculationResult.Incomplete)

        assertEquals(0.5, incompleteNode.hierarchyEdges.first().plannedWeight)
        assertEquals(0.0, incompleteNode.hierarchyEdges.first().actualWeight)
        assertEquals(0.5, incompleteNode.hierarchyEdges.last().plannedWeight)
        assertEquals(1.0, incompleteNode.hierarchyEdges.last().actualWeight)
    }

    @Test
    fun isValidResultRange() {
        (0..100).forEach { score ->
            val successResult = KpiCalculationResult.Success(score)
            val incompleteResult = KpiCalculationResult.Incomplete(score, "Incomplete")
            assertEquals(
                successResult,
                BaseKpiCalculationStrategy.getResultInValidRange(successResult),
            )
            assertEquals(
                incompleteResult,
                BaseKpiCalculationStrategy.getResultInValidRange(incompleteResult),
            )
        }
    }

    @Test
    fun isInvalidResultRange() {

        val smallerThanZero = List(10) { Random.nextInt(-100, 0) }
        val largerThanHundred = List(10) { Random.nextInt(101, 200) }

        smallerThanZero.forEach { score ->
            val successResult = KpiCalculationResult.Success(score)
            val incompleteResult = KpiCalculationResult.Incomplete(score, "Incomplete")
            assertEquals(
                KpiCalculationResult.Success(0),
                BaseKpiCalculationStrategy.getResultInValidRange(successResult),
            )
            assertEquals(
                KpiCalculationResult.Incomplete(0, "Incomplete"),
                BaseKpiCalculationStrategy.getResultInValidRange(incompleteResult),
            )
        }

        largerThanHundred.forEach { score ->
            val successResult = KpiCalculationResult.Success(score)
            val incompleteResult = KpiCalculationResult.Incomplete(score, "Incomplete")
            assertEquals(
                KpiCalculationResult.Success(100),
                BaseKpiCalculationStrategy.getResultInValidRange(successResult),
            )
            assertEquals(
                KpiCalculationResult.Incomplete(100, "Incomplete"),
                BaseKpiCalculationStrategy.getResultInValidRange(incompleteResult),
            )
        }
    }
}
