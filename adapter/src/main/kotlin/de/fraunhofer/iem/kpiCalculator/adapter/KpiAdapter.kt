package de.fraunhofer.iem.kpiCalculator.adapter

import de.fraunhofer.iem.kpiCalculator.model.adapter.AdapterResult


interface KpiAdapter<T> {
    fun transformDataToKpi(data: List<T>): List<AdapterResult>
    fun transformDataToKpi(data: T): List<AdapterResult> = transformDataToKpi(listOf(data))
}
