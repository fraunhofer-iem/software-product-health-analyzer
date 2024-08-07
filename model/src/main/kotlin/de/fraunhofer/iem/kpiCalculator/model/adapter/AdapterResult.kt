package de.fraunhofer.iem.kpiCalculator.model.adapter

import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

enum class ErrorType { DATA_VALIDATION_ERROR }

sealed class AdapterResult {
    data class Success(val rawValueKpi: RawValueKpi) : AdapterResult()
    data class Error(val type: ErrorType) : AdapterResult()
}
