/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.model.adapter.trivy

import de.fraunhofer.iem.kpiCalculator.model.adapter.vulnerability.VulnerabilityDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

data class TrivyDto(val Vulnerabilities: Collection<VulnerabilityDto>)

@Serializable
data class TrivyDtoV1(
    val Vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyDtoV2(
    val Results: List<Result> = listOf(),
    val SchemaVersion: Int
)

@Serializable
data class Result(
    val Vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyVulnerabilityDto(
    // NB: Because the names of its inner elements are not fixed, this needs to be a JsonObject.
    // This way we can iterate over those when required. Their type is always CVSSData.
    val CVSS: JsonObject?,
    val VulnerabilityID: String,
    val PkgID: String
)

@Serializable
data class CVSSData(
    val V2Score: Double?,
    val V3Score: Double?,
)
