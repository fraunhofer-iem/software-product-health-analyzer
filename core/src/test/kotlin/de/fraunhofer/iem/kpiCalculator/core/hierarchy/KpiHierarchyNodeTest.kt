package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.core.randomKpiHierarchyNode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

class KpiHierarchyNodeTest {

    @Test
    fun updateWeight() {
        val root = randomKpiHierarchyNode()

        val child = randomKpiHierarchyNode(root)

        root.addChild(node = child, weight = 1.0)
        (0..10).forEach { _ -> root.addChild(node = randomKpiHierarchyNode(), weight = 0.0) }

        val initialEdge = root.hierarchyEdges.find { it.to == child }
        assertNotNull(initialEdge)
        assertEquals(1.0, initialEdge.plannedWeight)
        assertEquals(1.0, initialEdge.actualWeight)

        root.updateWeight(node = child, newWeight = 0.5)

        val updatedEdge = root.hierarchyEdges.find { it.to == child }

        assertNotNull(updatedEdge)
        assertEquals(1.0, updatedEdge.plannedWeight)
        assertEquals(0.5, updatedEdge.actualWeight)
    }

    @Test
    fun updateWeightNoNode() {
        val root = randomKpiHierarchyNode()

        val child = randomKpiHierarchyNode(root)

        root.addChild(node = child, weight = 1.0)
        (0..10).forEach { _ -> root.addChild(node = randomKpiHierarchyNode(), weight = 0.0) }

        assertEquals(1.0, root.hierarchyEdges.first().plannedWeight)
        assertEquals(1.0, root.hierarchyEdges.first().actualWeight)
        assertEquals(12, root.hierarchyEdges.size)

        val nodeNotInHierarchy = randomKpiHierarchyNode()
        val noUpdatePossible = root.updateWeight(node = nodeNotInHierarchy, newWeight = 0.5)

        assertEquals(false, noUpdatePossible)

        assertEquals(1.0, root.hierarchyEdges.first().plannedWeight)
        assertEquals(1.0, root.hierarchyEdges.first().actualWeight)
        assertEquals(12, root.hierarchyEdges.size)
    }
}
