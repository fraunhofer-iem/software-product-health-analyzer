package de.fraunhofer.iem.kpiCalculator.model.kpi

import kotlinx.serialization.Serializable

@Serializable
data class RawValueKpi(val kind: KpiId, val score: Int)
