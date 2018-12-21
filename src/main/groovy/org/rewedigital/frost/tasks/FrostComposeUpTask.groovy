package org.rewedigital.frost.tasks

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class FrostComposeUpTask extends DockerComposeTask {

    @OutputDirectory
    def getReportDirectory() {
        Util.composeOutputDirectory(project)
    }

    @TaskAction
    @Override
    def action() {
        def env = [
                TAG: project.extensions[EXTENSION_NAME].sutTag
        ]

        if (project.extensions[EXTENSION_NAME].projectName) {
            env << [COMPOSE_PROJECT_NAME: project.extensions[EXTENSION_NAME].projectName]
        }

        def envString = env.inject([]) { result, entry -> result << "${entry.key}=${entry.value}" }.join(" ")
        println("env: ${envString}")

        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export ${envString} && ${EXECUTABLE} -f ${getComposeFile()} -f ${getComposeOverrideFile()} up"

        Util.executeAsynchronously(cmd, "docker_compose_up", getReportDirectory())
    }
}
