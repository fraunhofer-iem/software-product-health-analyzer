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
                kpiStrategyId = KpiStrategyId.WEIGHTED_RATIO_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = numberOfCommits, weight = 1.0),
                        KpiEdge(target = numberOfSignedCommits, weight = 1.0),
                    ),
            )

        val documentation =
            KpiNode(
                kpiId = KpiId.DOCUMENTATION,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = documentationInfrastructure, weight = 0.6),
                        KpiEdge(target = commentsInCode, weight = 0.4),
                    ),
            )

        val processComplianceKpi =
            KpiNode(
                kpiId = KpiId.PROCESS_COMPLIANCE,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
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
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges = listOf(KpiEdge(target = signedCommitsRatio, weight = 1.0)),
            )

        val codeVulnerabilities =
            KpiNode(
                kpiId = KpiId.CODE_VULNERABILITY_SCORE,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val maxDepVulnerability =
            KpiNode(
                kpiId = KpiId.MAXIMAL_VULNERABILITY,
                kpiStrategyId = KpiStrategyId.MINIMUM_STRATEGY,
                edges = listOf(KpiEdge(target = codeVulnerabilities, weight = 1.0)),
            )

        val containerVulnerabilities =
            KpiNode(
                kpiId = KpiId.CONTAINER_VULNERABILITY_SCORE,
                kpiStrategyId = KpiStrategyId.RAW_VALUE_STRATEGY,
                edges = listOf(),
            )

        val maxContainerVulnerability =
            KpiNode(
                kpiId = KpiId.MAXIMAL_VULNERABILITY,
                kpiStrategyId = KpiStrategyId.MAXIMUM_STRATEGY,
                edges = listOf(KpiEdge(target = containerVulnerabilities, weight = 1.0)),
            )

        val security =
            KpiNode(
                kpiId = KpiId.SECURITY,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges =
                    listOf(
                        KpiEdge(target = secrets, weight = 0.2),
                        KpiEdge(target = maxDepVulnerability, weight = 0.35),
                        KpiEdge(target = maxContainerVulnerability, weight = 0.35),
                        KpiEdge(target = checkedInBinaries, weight = 0.1),
                    ),
            )

        val internalQuality =
            KpiNode(
                kpiId = KpiId.INTERNAL_QUALITY,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges = listOf(KpiEdge(target = documentation, weight = 1.0)),
            )

        val externalQuality =
            KpiNode(
                kpiId = KpiId.EXTERNAL_QUALITY,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
                edges = listOf(KpiEdge(target = documentation, weight = 1.0)),
            )

        val root =
            KpiNode(
                kpiId = KpiId.ROOT,
                kpiStrategyId = KpiStrategyId.WEIGHTED_AVERAGE_STRATEGY,
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
