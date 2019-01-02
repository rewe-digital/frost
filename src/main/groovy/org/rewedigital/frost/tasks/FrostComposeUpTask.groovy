package org.rewedigital.frost.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

class FrostComposeUpTask extends DockerComposeTask {

    @OutputDirectory
    def getReportDirectory() {
        Util.composeOutputDirectory(project)
    }

    @TaskAction
    @Override
    def action() {
        def cmd = dockerComposeCommand("up")
        Util.executeAsynchronously(cmd, "docker_compose_up", getReportDirectory())
    }
}
