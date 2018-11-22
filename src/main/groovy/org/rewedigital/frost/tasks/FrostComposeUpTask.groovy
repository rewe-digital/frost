package org.rewedigital.frost.tasks

import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class FrostComposeUpTask extends DockerComposeTask {

    @TaskAction
    @Override
    def action() {
        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export TAG=${project.extensions[EXTENSION_NAME].sutTag} && ${EXECUTABLE} -f ${getComposeFile().getAbsolutePath()} -f ${getComposeOverrideFile().getAbsolutePath()} up"

        Util.executeAsynchronously(cmd, "docker_compose_up")
    }
}
