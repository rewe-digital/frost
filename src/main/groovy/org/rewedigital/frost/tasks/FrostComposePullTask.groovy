package org.rewedigital.frost.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class FrostComposePullTask extends DockerComposeTask {

    @OutputDirectory
    def getReportDirectory() {
        Util.composeOutputDirectory(project)
    }

    @TaskAction
    @Override
    def action() {
        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export TAG=${project.extensions[EXTENSION_NAME].sutTag} && ${EXECUTABLE} -f ${getComposeFile()} -f ${getComposeOverrideFile()} pull --ignore-pull-failures"

        Util.executeSynchronously(cmd, "docker_compose_pull", getReportDirectory())
    }
}
