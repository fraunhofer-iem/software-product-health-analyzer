package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc

import de.fraunhofer.iem.kpiCalculator.adapter.AdapterResult
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.ArtifactVersion
import de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model.Version
import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.*
import de.fraunhofer.iem.kpiCalculator.model.kpi.KpiId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class TlcAdapterTest {

    @Test
    fun transformDataToKpiEmpty() {
        val kpis =
            TlcAdapter.transformDataToKpi(
                data =
                    listOf(
                        TlcDto(
                            repositoryInfo =
                                RepositoryInfoDto(url = "", revision = "", projects = listOf()),
                            environmentInfo = EnvironmentInfoDto(ortVersion = "", javaVersion = ""),
                            projectDtos =
                                listOf(
                                    ProjectDto(
                                        artifacts = listOf(),
                                        graph = listOf(),
                                        ecosystem = "",
                                        version = "",
                                        artifactId = "",
                                        groupId = "",
                                    )
                                ),
                        )
                    )
            )

        assertEquals(0, kpis.size)
    }

    @Test
    fun transformSingleNodeToKpi() {

        val usedVersionDate =
            LocalDateTime(2024, 1, 1, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val patchVersionDate =
            LocalDateTime(2024, 1, 3, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val minorVersionDate =
            LocalDateTime(2024, 1, 9, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val majorVersionDate =
            LocalDateTime(2024, 1, 19, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()

        val versions =
            listOf(
                ArtifactVersionDto(
                    versionNumber = "3.11",
                    releaseDate = usedVersionDate,
                    isDefault = true,
                ),
                ArtifactVersionDto(
                    versionNumber = "3.11.3",
                    releaseDate = patchVersionDate,
                    isDefault = false,
                ),
                ArtifactVersionDto(versionNumber = "3.12", releaseDate = 0L, isDefault = false),
                ArtifactVersionDto(
                    versionNumber = "3.12.3",
                    releaseDate = minorVersionDate,
                    isDefault = false,
                ),
                ArtifactVersionDto(
                    versionNumber = "4.12.3",
                    releaseDate = majorVersionDate,
                    isDefault = false,
                ),
            )

        val kpis =
            TlcAdapter.transformDataToKpi(
                data =
                    listOf(
                        TlcDto(
                            repositoryInfo =
                                RepositoryInfoDto(url = "", revision = "", projects = listOf()),
                            environmentInfo = EnvironmentInfoDto(ortVersion = "", javaVersion = ""),
                            projectDtos =
                                listOf(
                                    ProjectDto(
                                        artifacts =
                                            listOf(
                                                ArtifactDto(
                                                    artifactId = "first",
                                                    groupId = "first group id",
                                                    versions = versions,
                                                )
                                            ),
                                        graph =
                                            listOf(
                                                ScopeToGraph(
                                                    scope = "dependencies",
                                                    graph =
                                                        DependencyGraphDto(
                                                            nodes =
                                                                listOf(
                                                                    DependencyNodeDto(0, "3.11")
                                                                ),
                                                            edges = listOf(),
                                                            directDependencyIndices = listOf(0),
                                                        ),
                                                )
                                            ),
                                        ecosystem = "NPM",
                                        version = "",
                                        artifactId = "",
                                        groupId = "",
                                    )
                                ),
                        )
                    )
            )

        assertEquals(1, kpis.size)

        val kpi = kpis.first()

        val isSuccess = kpi is AdapterResult.Success
        assertTrue(isSuccess)

        val isSuccessWithResult = kpi is AdapterResult.Success.KpiTechLag
        assertTrue(isSuccessWithResult)

        val rawValueKpi = (kpi as AdapterResult.Success).rawValueKpi

        assertEquals(KpiId.LIB_DAYS_PROD, rawValueKpi.kind)
        assertEquals(100, rawValueKpi.score)

        val techLag = (kpi as AdapterResult.Success.KpiTechLag)
        assertEquals(18, techLag.techLag.libyear)
    }

    private fun testVersion(
        expectedVersion: String,
        versions: List<ArtifactVersion>,
        targetVersion: Version,
        usedVersion: String,
    ) {

        val target =
            ArtifactVersion.getTargetVersion(
                usedVersion = usedVersion,
                updateType = targetVersion,
                versions = versions,
            )

        if (target == null) {
            fail("Target version is null")
        }
        assertEquals(expectedVersion, target.versionNumber)
    }

    @Test
    fun getTargetVersion() {
        val usedVersionDate =
            LocalDateTime(2024, 1, 1, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val patchVersionDate =
            LocalDateTime(2024, 1, 3, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val minorVersionDate =
            LocalDateTime(2024, 1, 9, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val majorVersionDate =
            LocalDateTime(2024, 1, 19, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val alphaVersionDate =
            LocalDateTime(2024, 1, 29, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()

        val versions =
            listOf(
                    ArtifactVersion.create(
                        versionNumber = "0.3.11",
                        releaseDate = usedVersionDate,
                        isDefault = true,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.11",
                        releaseDate = usedVersionDate,
                        isDefault = true,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.11.3",
                        releaseDate = patchVersionDate,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.12",
                        releaseDate = 0L,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.12.3",
                        releaseDate = minorVersionDate,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "4.12.3",
                        releaseDate = majorVersionDate,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "4.12.4-Alpha",
                        releaseDate = alphaVersionDate,
                        isDefault = false,
                    ),
                )
                .mapNotNull { it }

        testVersion("4.12.3", versions, Version.Major, "3.11")
        testVersion("3.12.3", versions, Version.Minor, "3.11")
        testVersion("3.11.3", versions, Version.Patch, "3.11")

        testVersion("4.12.3", versions, Version.Major, "3.11.3")
        testVersion("3.12.3", versions, Version.Minor, "3.12")
        testVersion("3.11.3", versions, Version.Patch, "3.11.3")

        testVersion("4.12.4-Alpha", versions, Version.Major, "3.11.3-Beta")
        testVersion("4.12.3", versions, Version.Major, "0.3.10")
        testVersion("0.3.11", versions, Version.Patch, "0.3.10")
    }

    @Test
    fun getTargetVersionUnknown() {
        val usedVersionDate =
            LocalDateTime(2024, 1, 1, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val patchVersionDate =
            LocalDateTime(2024, 1, 3, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val minorVersionDate =
            LocalDateTime(2024, 1, 9, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()
        val majorVersionDate =
            LocalDateTime(2024, 1, 19, 0, 0).toInstant(TimeZone.of("UTC+3")).toEpochMilliseconds()

        val versions =
            listOf(
                    ArtifactVersion.create(
                        versionNumber = "3.11",
                        releaseDate = usedVersionDate,
                        isDefault = true,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.11.3",
                        releaseDate = patchVersionDate,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.12",
                        releaseDate = 0L,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "3.12.3",
                        releaseDate = minorVersionDate,
                        isDefault = false,
                    ),
                    ArtifactVersion.create(
                        versionNumber = "4.12.3",
                        releaseDate = majorVersionDate,
                        isDefault = false,
                    ),
                )
                .mapNotNull { it }

        // the used version doesn't need to exist in the versions list in order to select the
        // correct
        // update target
        testVersion("4.12.3", versions, Version.Major, "2.11")
    }
}
