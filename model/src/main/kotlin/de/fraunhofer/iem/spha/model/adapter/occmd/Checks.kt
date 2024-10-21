/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.model.adapter.occmd

enum class Checks(val checkName: String) {
    SastUsageBasic("SastUsageBasic"),
    Secrets("Secrets"),
    CheckedInBinaries("CheckedInBinaries"),
    CommentsInCode("CommentsInCode"),
    DocumentationInfrastructure("ExistenceOfDocumentationInfrastructure");

    companion object {
        fun fromString(value: String): Checks? {
            return Checks.entries.find { it.checkName.equals(value, ignoreCase = true) }
        }
    }
}
