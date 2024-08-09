package de.fraunhofer.iem.kpiCalculator.adapter.occmd

import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.occmd.Checks
import de.fraunhofer.iem.kpiCalculator.model.adapter.occmd.OccmdDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

object OccmdAdapter : KpiAdapter<OccmdDto> {

    override fun transformDataToKpi(data: List<OccmdDto>): List<AdapterResult> {

        return data.mapNotNull {
            return@mapNotNull when (Checks.fromString(it.check)) {
                Checks.CheckedInBinaries ->
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = KpiId.CHECKED_IN_BINARIES,
                            score = (it.score * 100).toInt()
                        )
                    )

                Checks.SastUsageBasic ->
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = KpiId.SAST_USAGE,
                            score = (it.score * 100).toInt()
                        )
                    )

                Checks.Secrets ->
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = KpiId.SECRETS,
                            score = (it.score * 100).toInt()
                        )
                    )

                Checks.CommentsInCode ->
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = KpiId.COMMENTS_IN_CODE,
                            score = (it.score * 100).toInt()
                        )
                    )

                Checks.DocumentationInfrastructure ->
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = KpiId.DOCUMENTATION_INFRASTRUCTURE,
                            score = (it.score * 100).toInt()
                        )
                    )

                else -> {
                    null
                }
            }
        }
    }
}
