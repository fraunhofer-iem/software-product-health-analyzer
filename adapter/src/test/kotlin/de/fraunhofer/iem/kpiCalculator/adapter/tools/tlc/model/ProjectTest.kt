package de.fraunhofer.iem.kpiCalculator.adapter.tools.tlc.model

import de.fraunhofer.iem.kpiCalculator.model.adapter.tlc.ProjectDto
import kotlin.test.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ProjectTest {

    @Test
    fun emptyProject() {
        val projectDto =
            ProjectDto(
                artifacts = listOf(),
                graph = listOf(),
                ecosystem = "",
                version = "",
                artifactId = "",
                groupId = "",
            )
        assertDoesNotThrow { Project.from(projectDto = projectDto) }
    }
}
