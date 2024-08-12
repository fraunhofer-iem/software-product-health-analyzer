package de.fraunhofer.iem.kpiCalculator.adapter.kpis.vcs

import de.fraunhofer.iem.kpiCalculator.adapter.kpis.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.model.adapter.vcs.RepositoryDetailsDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

object VcsAdapter : KpiAdapter<RepositoryDetailsDto> {

    override fun transformDataToKpi(data: List<RepositoryDetailsDto>): List<AdapterResult> {

        if (data.size != 1) {
            return listOf(
                AdapterResult.Error(type = ErrorType.DATA_VALIDATION_ERROR)
            )
        }

        //XXX: we need to decide about error handling in adapters
        val repoDetailsDto = data.first()
        return listOf(
            AdapterResult.Success(
                RawValueKpi(
                    kind = KpiId.NUMBER_OF_COMMITS,
                    score = repoDetailsDto.numberOfCommits
                )
            ),
            AdapterResult.Success(
                RawValueKpi(
                    kind = KpiId.NUMBER_OF_SIGNED_COMMITS,
                    score = repoDetailsDto.numberOfSignedCommits
                )
            ),
            AdapterResult.Success(
                RawValueKpi(
                    kind = KpiId.IS_DEFAULT_BRANCH_PROTECTED,
                    score = if (repoDetailsDto.isDefaultBranchProtected) 100 else 0
                )
            )
        )
    }
}
