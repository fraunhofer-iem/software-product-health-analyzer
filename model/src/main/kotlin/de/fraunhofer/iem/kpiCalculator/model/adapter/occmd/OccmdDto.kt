/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.model.adapter.occmd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** The result of an occmd check performed on an OpenCodDE project. */
@Serializable
data class OccmdDto(
    @SerialName("check") val check: String,
    @SerialName("id") val id: Int,
    @SerialName("results") val results: JsonObject,
    @SerialName("score") val score: Double,
)
