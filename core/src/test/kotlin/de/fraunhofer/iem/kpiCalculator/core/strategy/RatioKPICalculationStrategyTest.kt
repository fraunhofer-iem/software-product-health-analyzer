package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RatioKPICalculationStrategyTest {

    @Test
    fun isValidEmpty() {
        val node = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
            edges = listOf()
        )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = false))
        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = true))

        val incorrectStrategy = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
            edges = listOf()
        )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = incorrectStrategy, strict = false))
        assertEquals(true, RatioKPICalculationStrategy.isValid(node = incorrectStrategy, strict = true))
    }

    @Test
    fun isValidCorrectChildren() {
        val nodeCorrectChildren = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
            edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                ),
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                )
            )
        )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = nodeCorrectChildren, strict = false))
        assertEquals(true, RatioKPICalculationStrategy.isValid(node = nodeCorrectChildren, strict = true))
    }

    @Test
    fun isValidToManyChildren() {
        val nodeManyChildren = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
            edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                ),
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                ),
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                )
            )
        )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = nodeManyChildren, strict = false))
        assertEquals(false, RatioKPICalculationStrategy.isValid(node = nodeManyChildren, strict = true))
    }


    @Test
    fun isValidToViewChildren() {
        val nodeToFewChildren = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
            edges = listOf(
                KpiEdge(
                    target = KpiNode(
                        kpiId = KpiId.NUMBER_OF_COMMITS,
                        kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                        edges = listOf()
                    ),
                    weight = 0.5
                ),
            )
        )

        assertEquals(false, RatioKPICalculationStrategy.isValid(node = nodeToFewChildren, strict = false))
        assertEquals(false, RatioKPICalculationStrategy.isValid(node = nodeToFewChildren, strict = true))
    }
}
