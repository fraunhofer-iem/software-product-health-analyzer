package de.fraunhofer.iem.kpiCalculator.adapter.tools.trivy

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class TrivyAdapterTest {

    @ParameterizedTest
    @ValueSource(strings = [
        "{}", // No schema
        "{\"SchemaVersion\": 3}" // Not supported schema
    ])
    fun testInvalidJson(input: String) {
        input.byteInputStream().use {
            assertThrows<UnsupportedOperationException> { TrivyAdapter.dtoFromJson(it) }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "[]",
        "{\"SchemaVersion\": 2}"
    ])
    fun testEmptyDto(input: String){
        input.byteInputStream().use {
            val dto =  TrivyAdapter.dtoFromJson(it)
            assertEquals(0, dto.Vulnerabilities.count())
        }
    }

    @Test
    fun testResult2Dto(){
        Files.newInputStream(Path("src/test/resources/trivy-result-v2.json")).use {
            val dto = assertDoesNotThrow { TrivyAdapter.dtoFromJson(it) }
            assertEquals(1, dto.Vulnerabilities.count())
        }
    }

    @Test
    fun testResult1Dto(){
        Files.newInputStream(Path("src/test/resources/trivy-result-v1.json")).use {
            val dto = assertDoesNotThrow { TrivyAdapter.dtoFromJson(it) }
            assertEquals(2, dto.Vulnerabilities.count())
        }
    }

}
