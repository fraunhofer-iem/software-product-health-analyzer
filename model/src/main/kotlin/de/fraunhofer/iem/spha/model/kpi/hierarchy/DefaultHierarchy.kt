/*
 * Copyright (c) 2024 Fraunhofer IEM. All rights reserved.
 *
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 *
 * SPDX-License-Identifier: MIT
 * License-Filename: LICENSE
 */

package de.fraunhofer.iem.spha.model.kpi.hierarchy

import de.fraunhofer.iem.spha.model.kpi.KpiId
import de.fraunhofer.iem.spha.model.kpi.KpiStrategyId

object DefaultHierarchy {
    fun get(): KpiHierarchy {

        val secrets =
            KpiNode(
                kpiId = KpiId.SECRETS,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val documentationInfrastructure =
            KpiNode(
                kpiId = KpiId.DOCUMENTATION_INFRASTRUCTURE,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val commentsInCode =
            KpiNode(
                kpiId = KpiId.COMMENTS_IN_CODE,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val numberOfCommits =
            KpiNode(
                kpiId = KpiId.NUMBER_OF_COMMITS,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val numberOfSignedCommits =
            KpiNode(
                kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val isDefaultBranchProtected =
            KpiNode(
                kpiId = KpiId.IS_DEFAULT_BRANCH_PROTECTED,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val checkedInBinaries =
            KpiNode(
                kpiId = KpiId.CHECKED_IN_BINARIES,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val signedCommitsRatio =
            KpiNode(
                kpiId = KpiId.SIGNED_COMMITS_RATIO,
                kpiStrategyId = KpiStrategyId.RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = numberOfCommits, weight = 1.0),
                        KpiEdge(target = numberOfSignedCommits, weight = 1.0),
                    ),
            )

        val documentation =
            KpiNode(
                kpiId = KpiId.DOCUMENTATION,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = documentationInfrastructure, weight = 0.6),
                        KpiEdge(target = commentsInCode, weight = 0.4),
                    ),
            )

        val processComplianceKpi =
            KpiNode(
                kpiId = KpiId.PROCESS_COMPLIANCE,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = checkedInBinaries, weight = 0.2),
                        KpiEdge(target = signedCommitsRatio, weight = 0.2),
                        KpiEdge(target = isDefaultBranchProtected, weight = 0.3),
                        KpiEdge(target = documentation, weight = 0.3),
                    ),
            )

        val processTransparency =
            KpiNode(
                kpiId = KpiId.PROCESS_TRANSPARENCY,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(KpiEdge(target = signedCommitsRatio, weight = 1.0)),
            )

        val vulnerabilities =
            KpiNode(
                kpiId = KpiId.VULNERABILITY_SCORE,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val maxDepVulnerability =
            KpiNode(
                kpiId = KpiId.MAXIMAL_VULNERABILITY,
                kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                edges = listOf(KpiEdge(target = vulnerabilities, weight = 1.0)),
            )

        val security =
            KpiNode(
                kpiId = KpiId.SECURITY,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = secrets, weight = 0.3),
                        KpiEdge(target = maxDepVulnerability, weight = 0.5),
                        KpiEdge(target = checkedInBinaries, weight = 0.2),
                    ),
            )

        val internalQuality =
            KpiNode(
                kpiId = KpiId.INTERNAL_QUALITY,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(KpiEdge(target = documentation, weight = 1.0)),
            )

        val externalQuality =
            KpiNode(
                kpiId = KpiId.EXTERNAL_QUALITY,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges = listOf(KpiEdge(target = documentation, weight = 1.0)),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.AGGREGATION_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = processTransparency, weight = 0.1),
                        KpiEdge(target = processComplianceKpi, weight = 0.1),
                        KpiEdge(target = security, weight = 0.4),
                        KpiEdge(target = internalQuality, weight = 0.15),
                        KpiEdge(target = externalQuality, weight = 0.25),
                    ),
            )

        return KpiHierarchy.create(root)
    }
}
