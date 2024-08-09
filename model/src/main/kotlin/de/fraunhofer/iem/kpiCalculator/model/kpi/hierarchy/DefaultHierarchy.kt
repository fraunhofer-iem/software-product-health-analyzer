package de.fraunhofer.iem.kpiCalculator.model.kpi.hierarchy

import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiStrategyId

object DefaultHierarchy {
    fun get(): KpiHierarchy {

        val secrets =
            KpiNode(kpiId = KpiId.SECRETS, strategyType = KpiStrategyId.RAW_VALUE_STRATEGY, children = listOf())

        val documentationInfrastructure = KpiNode(
            kpiId = KpiId.DOCUMENTATION_INFRASTRUCTURE,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val commentsInCode = KpiNode(
            kpiId = KpiId.COMMENTS_IN_CODE,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val numberOfCommits = KpiNode(
            kpiId = KpiId.NUMBER_OF_COMMITS,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val numberOfSignedCommits = KpiNode(
            kpiId = KpiId.NUMBER_OF_SIGNED_COMMITS,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val isDefaultBranchProtected = KpiNode(
            kpiId = KpiId.IS_DEFAULT_BRANCH_PROTECTED,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val checkedInBinaries = KpiNode(
            kpiId = KpiId.CHECKED_IN_BINARIES,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val signedCommitsRatio = KpiNode(
            kpiId = KpiId.SIGNED_COMMITS_RATIO,
            strategyType = KpiStrategyId.RATIO_STRATEGY,
            children = listOf(
                KpiEdge(target = numberOfCommits, weight = 1.0),
                KpiEdge(target = numberOfSignedCommits, weight = 1.0)
            )
        )

        val documentation = KpiNode(
            kpiId = KpiId.DOCUMENTATION,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = documentationInfrastructure, weight = 0.6),
                KpiEdge(target = commentsInCode, weight = 0.4),
            )
        )

        val processComplianceKpi = KpiNode(
            kpiId = KpiId.PROCESS_COMPLIANCE,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = checkedInBinaries, weight = 0.2),
                KpiEdge(target = signedCommitsRatio, weight = 0.2),
                KpiEdge(target = isDefaultBranchProtected, weight = 0.3),
                KpiEdge(target = documentation, weight = 0.3)
            )
        )

        val processTransparency = KpiNode(
            kpiId = KpiId.PROCESS_TRANSPARENCY,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = signedCommitsRatio, weight = 1.0)
            )
        )


        val vulnerabilities = KpiNode(
            kpiId = KpiId.VULNERABILITY_SCORE,
            strategyType = KpiStrategyId.RAW_VALUE_STRATEGY,
            children = listOf()
        )

        val maxDepVulnerability = KpiNode(
            kpiId = KpiId.MAXIMAL_VULNERABILITY,
            strategyType = KpiStrategyId.MAXIMUM_STRATEGY,
            children = listOf(
                KpiEdge(target = vulnerabilities, weight = 1.0),
            )
        )

        val security = KpiNode(
            kpiId = KpiId.SECURITY,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = secrets, weight = 0.3),
                KpiEdge(target = maxDepVulnerability, weight = 0.5),
                KpiEdge(target = checkedInBinaries, weight = 0.2),
            )
        )

        val internalQuality = KpiNode(
            kpiId = KpiId.INTERNAL_QUALITY,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = documentation, weight = 1.0)
            )
        )

        val externalQuality = KpiNode(
            kpiId = KpiId.EXTERNAL_QUALITY,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(target = documentation, weight = 1.0)
            )
        )

        val root = KpiNode(
            kpiId = KpiId.ROOT,
            strategyType = KpiStrategyId.AGGREGATION_STRATEGY,
            children = listOf(
                KpiEdge(
                    target = processTransparency,
                    weight = 0.1
                ),
                KpiEdge(
                    target = processComplianceKpi,
                    weight = 0.1
                ),
                KpiEdge(
                    target = security,
                    weight = 0.4
                ),
                KpiEdge(
                    target = internalQuality,
                    weight = 0.15
                ),
                KpiEdge(
                    target = externalQuality,
                    weight = 0.25
                )
            )
        )

        return KpiHierarchy.create(root)
    }
}
