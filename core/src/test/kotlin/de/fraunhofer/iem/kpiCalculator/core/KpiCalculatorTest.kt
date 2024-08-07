package de.fraunhofer.iem.kpiCalculator.core

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class KpiCalculatorTest {

    @Test
    fun calculateDefaultHierarchyKpis() {
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
        val res = KpiCalculator.calculateKpis(DefaultHierarchy.get(), rawValueKpis)
        println(res)
    }

    @Test
    fun calculateMaxKpis() {
        val rawValueKpis = listOf(
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 82),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 90),
            RawValueKpi(kind = KpiId.VULNERABILITY_SCORE, score = 65),
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT, strategyType = KpiStrategyId.AGGREGATION_STRATEGY, children = listOf(
                KpiEdge(
                    target =
                    KpiNode(
                        kpiId = KpiId.MAXIMAL_VULNERABILITY,
                        strategyType = KpiStrategyId.MAXIMUM_STRATEGY,
                        children = listOf(
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.VULNERABILITY_SCORE,
                                    strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    children = emptyList()
                                ), weight = 1.0
                            )
                        )
                    ), weight = 1.0
                )
            )
        )
        val hierarchy = KpiHierarchy.create(root)

        val res = KpiCalculator.calculateKpis(hierarchy, rawValueKpis)
        val result = res.kpiResult

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
            kpiId = KpiId.ROOT, strategyType = KpiStrategyId.AGGREGATION_STRATEGY, children = listOf(
                KpiEdge(
                    target =
                    KpiNode(
                        kpiId = KpiId.SECURITY,
                        strategyType = KpiStrategyId.MAXIMUM_STRATEGY,
                        children = listOf(
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.VULNERABILITY_SCORE,
                                    strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    children = emptyList()
                                ), weight = 0.5
                            ),
                            KpiEdge(
                                target = KpiNode(
                                    kpiId = KpiId.SAST_USAGE,
                                    strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
                                    children = emptyList()
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
        val result = res.kpiResult

        println(res)
        if (result is KpiCalculationResult.Incomplete) {
            assertEquals(90, result.score)
        } else {
            fail()
        }
    }
}
