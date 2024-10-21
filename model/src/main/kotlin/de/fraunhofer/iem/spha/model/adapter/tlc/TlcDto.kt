/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.model.adapter.tlc

import kotlinx.serialization.Serializable

@Serializable
data class TlcDto(
    val repositoryInfo: RepositoryInfoDto,
    val environmentInfo: EnvironmentInfoDto,
    val projectDtos: List<ProjectDto>,
)

@Serializable
data class RepositoryInfoDto(
    val url: String,
    val revision: String,
    val projects: List<ProjectInfoDto>,
)

@Serializable
data class ProjectInfoDto(
    val type: String,
    val namespace: String,
    val name: String,
    val version: String,
)

@Serializable data class EnvironmentInfoDto(val ortVersion: String, val javaVersion: String)
