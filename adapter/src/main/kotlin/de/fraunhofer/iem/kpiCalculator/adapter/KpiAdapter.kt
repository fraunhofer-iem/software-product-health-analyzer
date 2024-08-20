/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter

import de.fraunhofer.iem.kpiCalculator.model.kpi.RawValueKpi

/***
 * Adapter that transforms a list of elements into a list of raw KPI values.
 * @param T The type of the element that shall be transformed.
 */
interface KpiAdapter<T> {
    /**
     * Transforms a specified list of elements into a list of raw KPI values.
     * In the case of failure, the result may contain elements indicating the status of the failure.
     * @param data The elements that shall be transformed
     * @return The transformed elements.
     */
    fun transformDataToKpi(data: Collection<T>): Collection<AdapterResult>
    /**
     * Transforms an elements into a list of raw KPI values.
     * In the case of failure, the result indicates the status of the failures.
     * @param data The element that shall be transformed
     * @return The transformed elements.
     */
    fun transformDataToKpi(data: T): Collection<AdapterResult> = transformDataToKpi(listOf(data))
}

enum class ErrorType { DATA_VALIDATION_ERROR }

sealed class AdapterResult {
    data class Success(val rawValueKpi: RawValueKpi) : AdapterResult()
    data class Error(val type: ErrorType) : AdapterResult()
}
