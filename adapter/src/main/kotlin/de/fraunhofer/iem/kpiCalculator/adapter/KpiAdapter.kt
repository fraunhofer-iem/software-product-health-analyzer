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


enum class ErrorType { DATA_VALIDATION_ERROR }

sealed class AdapterResult {
    data class Success(val rawValueKpi: RawValueKpi) : AdapterResult()
    data class Error(val type: ErrorType) : AdapterResult()
}
