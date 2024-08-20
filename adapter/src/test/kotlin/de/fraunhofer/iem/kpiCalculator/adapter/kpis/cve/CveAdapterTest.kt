/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.kpis.cve

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.adapter.ErrorType
import de.fraunhofer.iem.kpiCalculator.model.adapter.vulnerability.VulnerabilityDto
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
        when (val kpi = validKpi.first()) {
            is AdapterResult.Success -> {
                assert(kpi.rawValueKpi.score in (0..100))
            }

            else -> {
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
