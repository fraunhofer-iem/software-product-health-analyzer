package de.fraunhofer.iem.kpiCalculator.adapter.cve

import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.model.adapter.VulnerabilityDto
import org.junit.jupiter.api.Test
import kotlin.test.fail

class CveAdapterTest {

    @Test
    fun basicVulnerabilityToKpiTransformation() {
        val adapter = CveAdapter
        // valid input
        val validKpi = adapter.transformDataToKpi(
            VulnerabilityDto(
                cveIdentifier = "not blank",
                packageName = "not blank",
                severity = 0.1
            )
        )
        when (validKpi) {
            is AdapterResult.Success -> {
                assert(validKpi.rawValueKpi.score in (0..100))
            }

            is AdapterResult.Error -> {
                fail()
            }
        }

        // invalid input
        val invalidKpis = CveAdapter.transformDataToKpi(
            listOf(
                VulnerabilityDto(
                    cveIdentifier = "not blank",
                    packageName = "",
                    severity = 0.1
                ),
                VulnerabilityDto(
                    cveIdentifier = "",
                    packageName = "not blank",
                    severity = 0.1
                ),
                VulnerabilityDto(
                    cveIdentifier = "not blank",
                    packageName = "not blank",
                    severity = -0.1
                ),
                VulnerabilityDto(
                    cveIdentifier = "not blank",
                    packageName = "not blank",
                    severity = 10.1
                )
            )
        )

        invalidKpis.forEach { invalidKpi ->
            when (invalidKpi) {
                is AdapterResult.Error -> {
                    assert(invalidKpi.type == ErrorType.DATA_VALIDATION_ERROR)
                }

                is AdapterResult.Success -> {
                    fail()
                }
            }
        }
    }
}
