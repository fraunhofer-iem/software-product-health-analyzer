package de.fraunhofer.iem.kpiCalculator.adapter.cve

import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.model.adapter.VulnerabilityDto
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

object CveAdapter : KpiAdapter<VulnerabilityDto> {
    override val kpiId: KpiId
        get() = KpiId.VULNERABILITY_SCORE

    override fun transformDataToKpi(data: List<VulnerabilityDto>): List<AdapterResult> {
        return data
            .map {
                return@map if (isValid(it)) {
                    AdapterResult.Success(
                        RawValueKpi(
                            kind = kpiId,
                            score = (it.severity * 10).toInt()
                        )
                    )
                } else {
                    AdapterResult.Error(ErrorType.DATA_VALIDATION_ERROR)
                }
            }
    }

    private fun isValid(data: VulnerabilityDto): Boolean {
        return (
            data.severity in 0.0..10.0 &&
                data.packageName.isNotBlank() &&
                data.cveIdentifier.isNotBlank()
            )
    }
}
