/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.kpiCalculator.adapter.tools

enum class SupportedTool {
    Occmd;

    companion object{
        fun fromName(name: String) : SupportedTool{
            try {
                return SupportedTool.valueOf(name)
            } catch (e: IllegalArgumentException){
                throw ToolNotFoundException("Unable to find tool result adapter for tool '$name'.")
            }
        }
    }
}

class ToolNotFoundException(message: String) : Exception(message)
