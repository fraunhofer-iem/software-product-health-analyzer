package de.fraunhofer.iem.kpiCalculator.model.adapter.vcs

data class RepositoryDetailsDto(
    val projectId: Long,
    val numberOfCommits: Int,
    val numberOfSignedCommits: Int,
    val isDefaultBranchProtected: Boolean
)
