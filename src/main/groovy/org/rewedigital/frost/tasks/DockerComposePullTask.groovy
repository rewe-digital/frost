package org.rewedigital.frost.tasks


import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.GalenPluginExtension.EXTENSION_NAME

class DockerComposePullTask extends DockerComposeTask {

    @TaskAction
    @Override
    def action() {
        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export TAG=${project.extensions[EXTENSION_NAME].sutTag} && ${EXECUTABLE} -f ${getComposeFile().getAbsolutePath()} -f ${getComposeOverrideFile().getAbsolutePath()} pull --ignore-pull-failures"

        Util.executeSynchronously(cmd, "docker_compose_pull")
    }
}
