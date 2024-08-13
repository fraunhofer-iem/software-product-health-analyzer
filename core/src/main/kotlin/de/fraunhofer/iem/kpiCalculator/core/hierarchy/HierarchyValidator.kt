package de.fraunhofer.iem.kpiCalculator.core.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy.KpiHierarchy

object HierarchyValidator {
    //TODO: return type will most likely be changed to a complex type comparably to KpiCalculationResult
    fun isValid(kpiHierarchy: KpiHierarchy, strict: Boolean = false): Boolean {
        // TODO: check the schema version and build a schema specific validation if necessary

        val root = kpiHierarchy.rootNode

        // handle empty hierarchies
        if (root.edges.isEmpty()) {
            if (strict) {
                return false
            } else {
                // TODO: log a warning here, empty hierarchies might be allowed but are unusual
            }
        }

        return true
    }

}
