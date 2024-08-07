package de.fraunhofer.iem.kpiCalculator.adapter

import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId


interface KpiAdapter<T> {
    val kpiId: KpiId

    fun transformDataToKpi(data: List<T>): List<AdapterResult>
    fun transformDataToKpi(data: T): AdapterResult = transformDataToKpi(listOf(data)).first()
}
