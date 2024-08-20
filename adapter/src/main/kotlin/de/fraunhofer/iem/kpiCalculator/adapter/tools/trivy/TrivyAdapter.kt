package de.fraunhofer.iem.kpiCalculator.adapter.tools.trivy

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.adapter.KpiAdapter
import de.fraunhofer.iem.kpiCalculator.model.adapter.trivy.TrivyDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

object TrivyAdapter : KpiAdapter<TrivyDto> {
    override fun transformDataToKpi(data: Collection<TrivyDto>): Collection<AdapterResult> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun dtoFromJson(jsonData: InputStream): TrivyDto {
        return Json.decodeFromStream(jsonData)
    }
}
