package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.tasks.TaskAction

class DockerComposeKillTask extends DockerComposeTask {

    @TaskAction
    @Override
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
