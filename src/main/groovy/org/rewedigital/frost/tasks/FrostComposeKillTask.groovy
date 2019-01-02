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
            def cmd = dockerComposeCommand("kill")
            Util.executeSynchronously(cmd, "docker_compose_kill", getReportDirectory())

            cmd = dockerComposeCommand("rm -f -v")
            Util.executeSynchronously(cmd, "docker_compose_rm", getReportDirectory())
        } finally {
            getComposeOverrideFile().delete()
        }
    }
}
