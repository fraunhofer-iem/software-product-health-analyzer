package de.fraunhofer.iem.kpiCalculator.core

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.fail

class KpiCalculatorTest {

    @Test
    fun calculateDefaultHierarchyKpis() {
        assertDoesNotThrow {
            val rawValueKpis = listOf(
                RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 8),
                RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 9),
                RawValueKpi(kind = KpiId.CHECKED_IN_BINARIES, score = 100),
                RawValueKpi(kind = KpiId.COMMENTS_IN_CODE, score = 80),
                RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 90),
                RawValueKpi(kind = KpiId.IS_DEFAULT_BRANCH_PROTECTED, score = 100),
                RawValueKpi(kind = KpiId.NUMBER_OF_SIGNED_COMMITS, score = 80),
                RawValueKpi(kind = KpiId.SECRETS, score = 80),
                RawValueKpi(kind = KpiId.SAST_USAGE, score = 80),
                RawValueKpi(kind = KpiId.DOCUMENTATION_INFRASTRUCTURE, score = 80),
            )
            KpiCalculator.calculateKpis(DefaultHierarchy.get(), rawValueKpis)
        }
    }

    @Test
    fun calculateMaxKpis() {
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 82),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 65),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, edges = listOf(
                KpiEdge(
                    target =
                    KpiNode(
                        kpiId = KpiId.MAXIMAL_VULNERABILITY,
                        kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                        edges = listOf(
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.VULNERABILITY_SCORE,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = emptyList()
                                ), weight = 1.0
                            )
                        )
                    ), weight = 1.0
                )
            )
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
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 82),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 65),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, edges = listOf(
                KpiEdge(
                    target =
                    KpiNode(
                        kpiId = KpiId.SECURITY,
                        kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                        edges = listOf(
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.VULNERABILITY_SCORE,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = emptyList()
                                ), weight = 0.5
                            ),
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.SAST_USAGE,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = emptyList()
                                ),
                                weight = 0.5
                            )
                        )
                    ), weight = 1.0
                )
            )
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
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 80),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.VULNERABILITY_SCORE,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 1.0
                ),
            )
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
    fun calculateAggregationKpisIncompleteMixedKinds() {
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 80),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.VULNERABILITY_SCORE,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                ),
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.SAST_USAGE,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                )
            )
        )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(85, result.score)
            val sastResult = res.rootNode.children.find { it.target.kpiId == KpiId.SAST_USAGE } ?: fail()
            assertEquals(0.0, sastResult.actualWeight)
            val vulnerabilityEdges = res.rootNode.children.filter { it.target.kpiId == KpiId.VULNERABILITY_SCORE }
            assertEquals(2, vulnerabilityEdges.size)
            assertEquals(vulnerabilityEdges.first().actualWeight, 0.5)
            assertEquals(vulnerabilityEdges[1].actualWeight, 0.5)
        } else {
            fail()
        }
    }

    @Test
    fun calculateAggregationKpisIncompleteMixedKindsNested() {
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 80),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.MAXIMAL_VULNERABILITY,
                        kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                        edges = listOf(
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.VULNERABILITY_SCORE,
                                    kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    edges = listOf()
                                ),
                                weight = 1.0
                            ),
                        )
                    ),
                    weight = 0.5
                ),
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.SAST_USAGE,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                )
            )
        )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.rootNode.kpiResult

        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(90, result.score)
            val sastResult = res.rootNode.children.find { it.target.kpiId == KpiId.SAST_USAGE } ?: fail()
            assertEquals(0.0, sastResult.actualWeight)
            val vulnerabilityEdges = res.rootNode.children.filter { it.target.kpiId == KpiId.MAXIMAL_VULNERABILITY }
            assertEquals(vulnerabilityEdges.first().actualWeight, 1.0)
            assertEquals(vulnerabilityEdges.first().plannedWeight, 0.5)
        } else {
            fail()
        }
    }
}
