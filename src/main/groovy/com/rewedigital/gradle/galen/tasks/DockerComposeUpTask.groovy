package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class DockerComposeUpTask extends DockerComposeTask {

    @TaskAction
    @Override
    def action() {
        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export TAG=${project.extensions[EXTENSION_NAME].sutTag} && ${EXECUTABLE} -f ${getComposeFile().getAbsolutePath()} -f ${getComposeOverrideFile().getAbsolutePath()} up"

        Util.executeAsynchronously(cmd, "docker_compose_up")
    }
}
