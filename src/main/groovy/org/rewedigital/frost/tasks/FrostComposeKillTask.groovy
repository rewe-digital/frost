package org.rewedigital.frost.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

class FrostComposeKillTask extends DockerComposeTask {

    @OutputDirectory
    def getReportDirectory() {
        Util.composeOutputDirectory(project)
    }

    @TaskAction
    @Override
    def action() {
        try {
            def cmd = [EXECUTABLE]
            cmd << "-f"
            cmd << getComposeFile()
            cmd << "-f"
            cmd << getComposeOverrideFile()
            cmd << "kill"
            Util.executeSynchronously(cmd, "docker_compose_kill", getReportDirectory())

            cmd = [EXECUTABLE]
            cmd << "-f"
            cmd << getComposeFile()
            cmd << "-f"
            cmd << getComposeOverrideFile()
            cmd << "rm"
            cmd << "-f"
            cmd << "-v"
            Util.executeSynchronously(cmd, "docker_compose_rm", getReportDirectory())
        } finally {
            getComposeOverrideFile().delete()
        }
    }
}
