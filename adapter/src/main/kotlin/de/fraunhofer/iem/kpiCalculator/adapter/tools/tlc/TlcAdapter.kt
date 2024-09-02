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
import de.fraunhofer.iem.kpiCalculator.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Project
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Version
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.util.TechLagHelper.getTechLagForGraph
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.TlcDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

sealed class TechLagResult {
    data class Success(val libyear: Long) : TechLagResult()
    data class Empty(val reason: String) : TechLagResult()
}

object TlcAdapter : KpiAdapter<TlcDto> {
    override fun transformDataToKpi(data: Collection<TlcDto>): Collection<AdapterResult> {
        return data.flatMap { tlcDto ->
            tlcDto.projectDtos.flatMap {
                val project = Project.from(it)
                project.graph.map { (scope, graph) ->

                    val techLag =
                        getTechLagForGraph(
                            graph = graph,
                            artifacts = project.artifacts,
                            targetVersion = Version.Major
                        )

                    if (techLag is TechLagResult.Success) {

                        val libyearScore = getLibyearScore(techLag.libyear)

                        val rawValueKpi = if (
                            isProductionScope(ecosystem = project.ecosystem, scope = scope)
                        ) {
                            RawValueKpi(
                                score = libyearScore,
                                kind = KpiId.LIB_DAYS_PROD
                            )
                        } else {
                            RawValueKpi(
                                score = libyearScore,
                                kind = KpiId.LIB_DAYS_DEV
                            )
                        }

                        return@map AdapterResult.Success(rawValueKpi)
                    }

                    return@map AdapterResult.Error(ErrorType.DATA_VALIDATION_ERROR)
                }
            }
        }
    }

    private fun getLibyearScore(libyear: Long): Int {
        // NB: these values need to be sanity checked
        // TODO: libyear thresholds should live in a config file
        return when (libyear) {
            in Int.MIN_VALUE..30 -> 100
            in 31..90 -> 75
            in 91..180 -> 50
            in 181..360 -> 25
            else -> 0
        }
    }

    private fun isProductionScope(scope: String, ecosystem: String): Boolean {
        return when (ecosystem) {
            "NPM" -> "dependencies" == scope
            else -> true
        }
    }

}
