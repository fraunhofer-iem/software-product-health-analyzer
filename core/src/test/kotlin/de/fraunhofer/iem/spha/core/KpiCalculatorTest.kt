/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.core

import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import de.fraunhofer.iem.spha.model.kpi.RawValueKpi
import de.fraunhofer.iem.spha.model.kpi.hierarchy.DefaultHierarchy
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiHierarchy
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiNode
import kotlin.test.assertEquals
import kotlin.test.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class KpiCalculatorTest {

    @Test
    fun calculateDefaultHierarchyKpis() {
        assertDoesNotThrow {
            val rawValueKpis =
                listOf(
                    RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 8),
                    RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 9),
                    RawValueKpi(kpiId = KpiId.CHECKED_IN_BINARIES.name, score = 100),
                    RawValueKpi(kpiId = KpiId.COMMENTS_IN_CODE.name, score = 80),
                    RawValueKpi(kpiId = KpiId.NUMBER_OF_COMMITS.name, score = 90),
                    RawValueKpi(kpiId = KpiId.IS_DEFAULT_BRANCH_PROTECTED.name, score = 100),
                    RawValueKpi(kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS.name, score = 80),
                    RawValueKpi(kpiId = KpiId.SECRETS.name, score = 80),
                    RawValueKpi(kpiId = KpiId.SAST_USAGE.name, score = 80),
                    RawValueKpi(kpiId = KpiId.DOCUMENTATION_INFRASTRUCTURE.name, score = 80),
                )
            KpiCalculator.calculateKpis(DefaultHierarchy.get(), rawValueKpis)
        }
    }

    @Test
    fun calculateMaxKpis() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 82),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 65),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.MAXIMAL_VULNERABILITY.name,
                                    kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = emptyList(),
                                                    ),
                                                weight = 1.0,
                                            )
                                        ),
                                ),
                            weight = 1.0,
                        )
                    ),
            )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Success) {
            assertEquals(90, result.score)
        } else {
            fail()
        }
    }

    @Test
    fun calculateMaxKpisIncomplete() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 82),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 65),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SECURITY.name,
                                    kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = emptyList(),
                                                    ),
                                                weight = 1.0,
                                            ),
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.SAST_USAGE.name,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = emptyList(),
                                                    ),
                                                weight = 1.0,
                                            ),
                                        ),
                                ),
                            weight = 1.0,
                        )
                    ),
            )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(90, result.score)
        } else {
            fail()
        }
    }

    @Test
    fun calculateRatioKpisIncomplete() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 82),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 65),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SIGNED_COMMITS_RATIO.name,
                                    kpiStrategyId = KpiStrategyId.WEIGHTED_RATIO_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.NUMBER_OF_COMMITS.name,
                                                        edges = emptyList(),
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                    ),
                                                weight = 1.0,
                                            ),
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS.name,
                                                        edges = emptyList(),
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                    ),
                                                weight = 1.0,
                                            ),
                                        ),
                                ),
                            weight = 0.5,
                        ),
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SECURITY.name,
                                    kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = emptyList(),
                                                    ),
                                                weight = 0.5,
                                            )
                                        ),
                                ),
                            weight = 0.5,
                        ),
                    ),
            )

        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(90, result.score)
        } else {
            fail()
        }
    }

    @Test
    fun calculateAggregation() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 80),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 1.0,
                        )
                    ),
            )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Success) {
            assertEquals(85, result.score)
        } else {
            fail()
        }
    }

    @Test
    fun calculateAggregationKpisIncompleteMixedKpiIds() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 80),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 0.5,
                        ),
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SAST_USAGE.name,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 0.5,
                        ),
                    ),
            )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(85, result.score)
            val sastResult =
                res.rootNode.children.find { it.target.kpiId == KpiId.SAST_USAGE.name } ?: fail()
            assertEquals(0.0, sastResult.actualWeight)
            val vulnerabilityEdges =
                res.rootNode.children.filter {
                    it.target.kpiId == KpiId.CODE_VULNERABILITY_SCORE.name
                }
            assertEquals(2, vulnerabilityEdges.size)
            assertEquals(vulnerabilityEdges.first().actualWeight, 0.5)
            assertEquals(vulnerabilityEdges[1].actualWeight, 0.5)
        } else {
            fail()
        }
    }

    @Test
    fun calculateAggregationKpisIncompleteMixedkpiIdsNested() {
        val rawValueKpis =
            listOf(
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 80),
                RawValueKpi(kpiId = KpiId.CODE_VULNERABILITY_SCORE.name, score = 90),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT.name,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.MAXIMAL_VULNERABILITY.name,
                                    kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                                    edges =
                                        listOf(
                                            KpiEdge(
                                                target =
                                                    KpiNode(
                                                        kpiId = KpiId.CODE_VULNERABILITY_SCORE.name,
                                                        kpiStrategyId =
                                                            KpiStrategyId.RAW_VALUE_STRATEGY,
                                                        edges = listOf(),
                                                    ),
                                                weight = 1.0,
                                            )
                                        ),
                                ),
                            weight = 0.5,
                        ),
                        KpiEdge(
                            target =
                                KpiNode(
                                    kpiId = KpiId.SAST_USAGE.name,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf(),
                                ),
                            weight = 0.5,
                        ),
                    ),
            )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(90, result.score)
            val sastResult =
                res.rootNode.children.find { it.target.kpiId == KpiId.SAST_USAGE.name } ?: fail()
            assertEquals(0.0, sastResult.actualWeight)
            val vulnerabilityEdges =
                res.rootNode.children.filter { it.target.kpiId == KpiId.MAXIMAL_VULNERABILITY.name }
            assertEquals(vulnerabilityEdges.first().actualWeight, 1.0)
            assertEquals(vulnerabilityEdges.first().plannedWeight, 0.5)
        } else {
            fail()
        }
    }
}
