/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.adapter.kpis.vcs

import de.fraunhofer.iem.spha.adapter.AdapterResult
import de.fraunhofer.iem.spha.adapter.ErrorType
import de.fraunhofer.iem.spha.model.adapter.vcs.RepositoryDetailsDto
import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.RawValueKpi

object VcsAdapter {

    fun transformDataToKpi(data: Collection<RepositoryDetailsDto>): Collection<AdapterResult> {

        if (data.size != 1) {
            return listOf(AdapterResult.Error(type = ErrorType.DATA_VALIDATION_ERROR))
        }

        // XXX: we need to decide about error handling in adapters
        val repoDetailsDto = data.first()
        return listOf(
            AdapterResult.Success.Kpi(
                RawValueKpi(kind = KpiId.NUMBER_OF_COMMITS, score = repoDetailsDto.numberOfCommits)
            ),
            AdapterResult.Success.Kpi(
                RawValueKpi(
                    kind = KpiId.NUMBER_OF_SIGNED_COMMITS,
                    score = repoDetailsDto.numberOfSignedCommits,
                )
            ),
            AdapterResult.Success.Kpi(
                RawValueKpi(
                    kind = KpiId.IS_DEFAULT_BRANCH_PROTECTED,
                    score = if (repoDetailsDto.isDefaultBranchProtected) 100 else 0,
                )
            ),
        )
    }
}
