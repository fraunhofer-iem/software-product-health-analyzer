package de.fraunhofer.iem.kpiCalculator.model.adapter

import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.Range
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.RangeThreshold
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.TlcConfig
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows

class TlcAdapterConfigTest {

    @Test
    fun tlcConfigTest() {
        TlcConfig(
            thresholds =
                listOf(
                    RangeThreshold(score = 10, Range(from = 101, to = 110)),
                    RangeThreshold(score = 50, Range(from = 0, to = 100)),
                )
        )
    }

    @Test
    fun tlcInvalidConfigTest() {
        assertThrows<IllegalArgumentException> {
            TlcConfig(thresholds = listOf(RangeThreshold(score = 200, Range(from = 0, to = 100))))
        }

        assertThrows<IllegalArgumentException> {
            TlcConfig(
                thresholds =
                    listOf(
                        RangeThreshold(score = 50, Range(from = 10, to = 50)),
                        RangeThreshold(score = 50, Range(from = 0, to = 100)),
                    )
            )
        }
    }
}
