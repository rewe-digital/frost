package org.rewedigital.frost.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

class FrostComposePullTask extends DockerComposeTask {

    @OutputDirectory
    def getReportDirectory() {
        Util.composeOutputDirectory(project)
    }

    @TaskAction
    @Override
    def action() {
        def cmd = dockerComposeCommand("pull --ignore-pull-failures")
        Util.executeSynchronously(cmd, "docker_compose_pull", getReportDirectory())
    }
}
