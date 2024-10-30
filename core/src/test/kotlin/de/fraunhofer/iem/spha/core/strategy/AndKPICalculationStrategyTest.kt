package de.fraunhofer.iem.spha.core.strategy

import de.fraunhofer.iem.spha.core.hierarchy.KpiHierarchyNode
import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import de.fraunhofer.iem.spha.model.kpi.RawValueKpi
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiCalculationResult
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiEdge
import de.fraunhofer.iem.spha.model.kpi.hierarchy.KpiNode
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.*

class AndKPICalculationStrategyTest {

    @Test
    fun emptyEdges() {
        assertEquals(
            KpiCalculationResult.Empty(),
            AndKPICalculationStrategy.calculateKpi(listOf(), strict = true),
        )
        assertEquals(
            KpiCalculationResult.Empty(),
            AndKPICalculationStrategy.calculateKpi(listOf(), strict = false),
        )
    }

    @Test
    fun calculateCorrectFalse() {
        val root =
            KpiHierarchyNode.from(
                KpiNode(
                    kpiId = KpiId.ROOT,
                    kpiStrategyId = KpiStrategyId.XOR_STRATEGY,
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
                    RawValueKpi(kind = KpiId.NUMBER_OF_SIGNED_COMMITS, score = 100),
                    RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 20),
                ),
            )

        val calcRelaxed =
            AndKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = false,
            )

        val calcStrict =
            AndKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = true,
            )

        assertEquals(true, calcStrict is KpiCalculationResult.Success)
        assertEquals(true, calcRelaxed is KpiCalculationResult.Success)

        assertEquals(0, (calcStrict as KpiCalculationResult.Success).score)
        assertEquals(0, (calcRelaxed as KpiCalculationResult.Success).score)
    }

    @Test
    fun calculateCorrect() {
        val root =
            KpiHierarchyNode.from(
                KpiNode(
                    kpiId = KpiId.ROOT,
                    kpiStrategyId = KpiStrategyId.XOR_STRATEGY,
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
                    RawValueKpi(kind = KpiId.NUMBER_OF_SIGNED_COMMITS, score = 100),
                    RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = 100),
                ),
            )

        val calcRelaxed =
            AndKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = false,
            )

        val calcStrict =
            AndKPICalculationStrategy.calculateKpi(
                hierarchyEdges = root.hierarchyEdges,
                strict = true,
            )

        assertEquals(true, calcStrict is KpiCalculationResult.Success)
        assertEquals(true, calcRelaxed is KpiCalculationResult.Success)

        assertEquals(100, (calcStrict as KpiCalculationResult.Success).score)
        assertEquals(100, (calcRelaxed as KpiCalculationResult.Success).score)
    }
}
