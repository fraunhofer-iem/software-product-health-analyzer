package de.fraunhofer.iem.kpiCalculator.adapter

import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

interface KpiAdapter<T> {
    fun transformDataToKpi(data: List<T>): List<AdapterResult>
    fun transformDataToKpi(data: T): List<AdapterResult> = transformDataToKpi(listOf(data))
}

enum class ErrorType { DATA_VALIDATION_ERROR }

sealed class AdapterResult {
    data class Success(val rawValueKpi: RawValueKpi) : AdapterResult()
    data class Error(val type: ErrorType) : AdapterResult()
}
