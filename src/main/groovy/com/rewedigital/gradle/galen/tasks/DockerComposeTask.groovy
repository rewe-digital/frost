package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

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
}
