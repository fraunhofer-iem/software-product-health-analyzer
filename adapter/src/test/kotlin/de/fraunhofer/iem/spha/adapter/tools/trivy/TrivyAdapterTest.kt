/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.adapter.tools.trivy

import de.fraunhofer.iem.spha.adapter.kpis.cve.CveAdapter
import de.fraunhofer.iem.spha.model.adapter.trivy.TrivyDto
import de.fraunhofer.iem.spha.model.adapter.vulnerability.VulnerabilityDto
import io.mockk.mockkObject
import io.mockk.verify
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TrivyAdapterTest {

    @ParameterizedTest
    @ValueSource(
        strings =
            [
                "{}", // No schema
                "{\"SchemaVersion\": 3}", // Not supported schema
            ]
    )
    fun testInvalidJson(input: String) {
        input.byteInputStream().use {
            assertThrows<UnsupportedOperationException> { TrivyAdapter.dtoFromJson(it) }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["[]", "{\"SchemaVersion\": 2}"])
    fun testEmptyDto(input: String) {
        input.byteInputStream().use {
            val dto = TrivyAdapter.dtoFromJson(it)
            assertEquals(0, dto.vulnerabilities.count())
        }
    }

    @Test
    fun testResult2Dto() {
        Files.newInputStream(Path("src/test/resources/trivy-result-v2.json")).use {
            val dto = assertDoesNotThrow { TrivyAdapter.dtoFromJson(it) }
            assertEquals(2, dto.vulnerabilities.count())

            val vuln = dto.vulnerabilities.first()
            assertEquals("CVE-2011-3374", vuln.cveIdentifier)
            assertEquals("apt@2.6.1", vuln.packageName)
            assertEquals(4.3, vuln.severity)
        }
    }

    @Test
    fun testResult1Dto() {
        Files.newInputStream(Path("src/test/resources/trivy-result-v1.json")).use {
            val dto = assertDoesNotThrow { TrivyAdapter.dtoFromJson(it) }
            assertEquals(2, dto.vulnerabilities.count())

            assertTrue { dto.vulnerabilities.all { it.cveIdentifier == "CVE-2005-2541" } }
            assertEquals("tar@1.34+dfsg-1.2", dto.vulnerabilities.first().packageName)
            assertEquals(10.0, dto.vulnerabilities.first().severity)
        }
    }

    @Test
    fun testDto2Kpi_VerifyCveAdapterGetsCalled() {
        mockkObject(CveAdapter)
        val vulns =
            listOf(
                VulnerabilityDto("CVE-1", "A", "1.0", severity = 1.0),
                VulnerabilityDto("CVE-2", "B", "2.0", severity = 2.0),
                VulnerabilityDto("CVE-3", "C", "1.3", severity = 1.3),
            )

        TrivyAdapter.transformDataToKpi(TrivyDto(vulns))
        TrivyAdapter.transformDataToKpi(listOf(TrivyDto(vulns)))
        verify(exactly = 2) { CveAdapter.transformContainerVulnerabilityToKpi(vulns) }
    }
}
