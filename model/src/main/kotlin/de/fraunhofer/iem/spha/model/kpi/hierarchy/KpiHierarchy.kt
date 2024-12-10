/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.model.kpi.hierarchy

import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId
import kotlinx.serialization.Serializable

val SCHEMA_VERSIONS: Array<String> = arrayOf("1.0.0").sortedArray()

// XXX: add Hierarchy Validator
@ConsistentCopyVisibility
@Serializable
data class KpiHierarchy private constructor(val rootNode: KpiNode, val schemaVersion: String) {
    companion object {
        fun create(rootNode: KpiNode) = KpiHierarchy(rootNode, SCHEMA_VERSIONS.last())
    }
}

@Serializable
data class KpiNode(val kpiId: String, val kpiStrategyId: KpiStrategyId, val edges: List<KpiEdge>)

@Serializable data class KpiEdge(val target: KpiNode, val weight: Double)
