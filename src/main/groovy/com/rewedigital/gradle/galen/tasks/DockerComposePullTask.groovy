package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class DockerComposePullTask extends DefaultTask {

    static final EXECUTABLE = "docker-compose"


    @InputFile
    def getComposeFile() {
        Util.composeFile(project)
    }

    @InputFile
    def getComposeOverrideFile() {
        Util.composeOverrideFile(project)
    }

    @TaskAction
    def action() {
        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export TAG=${project.extensions[EXTENSION_NAME].sutTag} && ${EXECUTABLE} -f ${getComposeFile().getAbsolutePath()} -f ${getComposeOverrideFile ().getAbsolutePath()} pull --ignore-pull-failures"

        Util.executeSynchronously(cmd, "docker_compose_pull")
    }
}
