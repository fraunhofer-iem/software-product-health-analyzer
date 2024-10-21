/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.model.adapter.trivy

import de.fraunhofer.iem.spha.model.adapter.vulnerability.VulnerabilityDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

data class TrivyDto(val vulnerabilities: Collection<VulnerabilityDto>)

@Serializable
data class TrivyDtoV1(
    @SerialName("Vulnerabilities") val vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyDtoV2(
    @SerialName("Results") val results: List<Result> = listOf(),
    @SerialName("SchemaVersion") val schemaVersion: Int,
)

@Serializable
data class Result(
    @SerialName("Vulnerabilities") val vulnerabilities: List<TrivyVulnerabilityDto> = listOf()
)

@Serializable
data class TrivyVulnerabilityDto(
    // NB: Because the names of its inner elements are not fixed, this needs to be a JsonObject.
    // This way we can iterate over those when required. Their type is always CVSSData.
    @SerialName("CVSS") val cvss: JsonObject?,
    @SerialName("VulnerabilityID") val vulnerabilityID: String,
    @SerialName("PkgID") val pkgID: String,
)

@Serializable
data class CVSSData(
    @SerialName("V2Score") val v2Score: Double?,
    @SerialName("V3Score") val v3Score: Double?,
)
