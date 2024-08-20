/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.core.hierarchy

internal data class KpiHierarchyEdge(
    val from: KpiHierarchyNode,
    val to: KpiHierarchyNode,
    val plannedWeight: Double,
    val actualWeight: Double = plannedWeight,
)
