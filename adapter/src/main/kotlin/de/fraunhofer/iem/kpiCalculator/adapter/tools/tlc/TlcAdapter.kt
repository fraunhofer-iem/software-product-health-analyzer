/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Project
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.TlcDto

object TlcAdapter : KpiAdapter<TlcDto> {
    override fun transformDataToKpi(data: Collection<TlcDto>): Collection<AdapterResult> {
        // 1. transform DTOs to calculation objects
        data.forEach { tlcDto ->
            val projects = tlcDto.projectDtos.map { Project.from(it) }

        }
        TODO("Not yet implemented")
    }
}
