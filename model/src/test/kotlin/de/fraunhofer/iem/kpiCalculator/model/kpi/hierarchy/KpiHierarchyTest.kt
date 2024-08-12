package de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class KpiHierarchyTest {
    @Test
    fun constructHierarchy() {
        // This test doesn't really test any functionality. It's mainly here as a reminder to fail whenever we change something
        // related to our external data model (the KpiHierarchy), as this is what the library users store and use to call the
        // library with.
        // TLDR; Whenever this test fails we have a breaking change in how we construct our KPI hierarchy meaning we potentially
        // break our clients code.
        assertDoesNotThrow {
            val childNodes = listOf(
                KpiEdge(
                    KpiNode(kpiId = KpiId.SECURITY, KpiStrategyId.AGGREGATION_STRATEGY, listOf()),
                    weight = 0.3
                ),
                KpiEdge(
                    KpiNode(kpiId = KpiId.PROCESS_COMPLIANCE, KpiStrategyId.AGGREGATION_STRATEGY, listOf()),
                    weight = 0.3
                ),
                KpiEdge(
                    KpiNode(kpiId = KpiId.INTERNAL_QUALITY, KpiStrategyId.AGGREGATION_STRATEGY, listOf()),
                    weight = 0.3
                ),
            )
            val root = KpiNode(kpiId = KpiId.ROOT, kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY, childNodes)
            val hierarchy = KpiHierarchy.create(root)

            val json = Json {
                prettyPrint = true
            }
            val jsonResult = json.encodeToString(hierarchy)

            println(jsonResult)
            assertEquals(hierarchy.schemaVersion, "1.0.0")
        }
    }

}
