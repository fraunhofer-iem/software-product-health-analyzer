/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools.occmd

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.occmd.Checks
import de.fraunhofer.iem.kpiCalculator.model.adapter.occmd.OccmdDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

object OccmdAdapter {

    fun transformDataToKpi(data: Collection<OccmdDto>): Collection<AdapterResult> {

        return data.mapNotNull {
            return@mapNotNull when (Checks.fromString(it.check)) {
                Checks.CheckedInBinaries ->
                    AdapterResult.Success.Kpi(
                        RawValueKpi(
                            kind = KpiId.CHECKED_IN_BINARIES,
                            score = (it.score * 100).toInt(),
                        )
                    )

                Checks.SastUsageBasic ->
                    AdapterResult.Success.Kpi(
                        RawValueKpi(kind = KpiId.SAST_USAGE, score = (it.score * 100).toInt())
                    )

                Checks.Secrets ->
                    AdapterResult.Success.Kpi(
                        RawValueKpi(kind = KpiId.SECRETS, score = (it.score * 100).toInt())
                    )

                Checks.CommentsInCode ->
                    AdapterResult.Success.Kpi(
                        RawValueKpi(kind = KpiId.COMMENTS_IN_CODE, score = (it.score * 100).toInt())
                    )

                Checks.DocumentationInfrastructure ->
                    AdapterResult.Success.Kpi(
                        RawValueKpi(
                            kind = KpiId.DOCUMENTATION_INFRASTRUCTURE,
                            score = (it.score * 100).toInt(),
                        )
                    )

                else -> {
                    null
                }
            }
        }
    }
}
