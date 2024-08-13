package de.fraunhofer.iem.kpiCalculator.core.strategy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiNode
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RatioKPICalculationStrategyTest {

    @Test
    fun isValid() {
        val node = KpiNode(
            kpiId = KpiId.ROOT,
            kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
            edges = listOf()
        )

        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = false))
        assertEquals(true, RatioKPICalculationStrategy.isValid(node = node, strict = true))
    }
}