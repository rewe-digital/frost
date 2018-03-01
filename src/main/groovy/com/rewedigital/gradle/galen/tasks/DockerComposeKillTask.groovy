package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class DockerComposeKillTask extends DefaultTask {

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
        try {
            def cmd = [EXECUTABLE]
            cmd << "-f"
            cmd << getComposeFile().getAbsolutePath()
            cmd << "-f"
            cmd << getComposeOverrideFile().getAbsolutePath()
            cmd << "kill"
            Util.executeSynchronously(cmd, "docker_compose_kill")

            cmd = [EXECUTABLE]
            cmd << "-f"
            cmd << getComposeFile().getAbsolutePath()
            cmd << "-f"
            cmd << getComposeOverrideFile().getAbsolutePath()
            cmd << "rm"
            cmd << "-f"
            cmd << "-v"
            Util.executeSynchronously(cmd, "docker_compose_rm")
        } finally {
            getComposeOverrideFile().delete()
        }
    }
}
