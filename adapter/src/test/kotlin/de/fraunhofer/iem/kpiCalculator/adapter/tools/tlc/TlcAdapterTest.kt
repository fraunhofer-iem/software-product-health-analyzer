package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.*
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TlcAdapterTest {

    @Test
    fun transformDataToKpiEmpty() {
        val kpis = TlcAdapter.transformDataToKpi(
            data = listOf(
                TlcDto(
                    repositoryInfo = RepositoryInfoDto(
                        url = "",
                        revision = "",
                        projects = listOf()
                    ),
                    environmentInfo = EnvironmentInfoDto(
                        ortVersion = "",
                        javaVersion = "",
                        os = ""
                    ),
                    projectDtos = listOf(
                        ProjectDto(
                            artifacts = listOf(),
                            graph = listOf(),
                            ecosystem = "",
                            version = "",
                            artifactId = "",
                            groupId = ""
                        )
                    )
                )
            )
        )

        assertEquals(0, kpis.size)
    }


    @Test
    fun transformDataToKpi() {

        val usedVersionDate = LocalDateTime(2024, 1, 1, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val patchVersionDate = LocalDateTime(2024, 1, 3, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val minorVersionDate = LocalDateTime(2024, 1, 9, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val majorVersionDate = LocalDateTime(2024, 1, 19, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()

        val versions = listOf(
            ArtifactVersionDto(versionNumber = "3.11", releaseDate = usedVersionDate, isDefault = true),
            ArtifactVersionDto(versionNumber = "3.11.3", releaseDate = patchVersionDate, isDefault = false),
            ArtifactVersionDto(versionNumber = "3.12", releaseDate = 0L, isDefault = false),
            ArtifactVersionDto(versionNumber = "3.12.3", releaseDate = minorVersionDate, isDefault = false),
            ArtifactVersionDto(versionNumber = "4.12.3", releaseDate = majorVersionDate, isDefault = false),
        )


        val kpis = TlcAdapter.transformDataToKpi(
            data = listOf(
                TlcDto(
                    repositoryInfo = RepositoryInfoDto(
                        url = "",
                        revision = "",
                        projects = listOf()
                    ),
                    environmentInfo = EnvironmentInfoDto(
                        ortVersion = "",
                        javaVersion = "",
                        os = ""
                    ),
                    projectDtos = listOf(
                        ProjectDto(
                            artifacts = listOf(
                                ArtifactDto(
                                    artifactId = "first",
                                    groupId = "first group id",
                                    versions = versions
                                )
                            ),
                            graph = listOf(
                                ScopeToGraph(
                                    scope = "dependencies",
                                    graph = DependencyGraphDto(
                                        nodes = listOf(
                                            DependencyNodeDto(0, "3.11")
                                        ),
                                        edges = listOf(),
                                        directDependencyIndices = listOf(0)
                                    )
                                )
                            ),
                            ecosystem = "NPM",
                            version = "",
                            artifactId = "",
                            groupId = ""
                        )
                    )
                )
            )
        )

        assertEquals(1, kpis.size)

        val kpi = kpis.first()

        val isSuccess = kpi is AdapterResult.Success
        assertTrue(isSuccess)

        val rawValueKpi = (kpi as AdapterResult.Success).rawValueKpi

        assertEquals(KpiId.LIB_DAYS_PROD, rawValueKpi.kind)
        assertEquals(100, rawValueKpi.score)
    }
}