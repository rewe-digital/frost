package org.rewedigital.frost.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

abstract class DockerComposeTask extends DefaultTask {

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
    def abstract action()


    List<String> dockerComposeCommand(String command) {
        def envString = environment().inject([]) { result, entry -> result << "${entry.key}=${entry.value}" }.join(" ")

        def cmd = ["/bin/sh"]
        cmd << "-c"
        cmd << "export ${envString} && ${EXECUTABLE} -f ${getComposeFile()} -f ${getComposeOverrideFile()} ${command}"
        cmd
    }

    Map<String, String> environment() {
        def env = [
                TAG: project.extensions[EXTENSION_NAME].sutTag
        ]

        if (project.extensions[EXTENSION_NAME].projectName) {
            env << [COMPOSE_PROJECT_NAME: project.extensions[EXTENSION_NAME].projectName]
        }
        env
    }
}
