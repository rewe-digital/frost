package com.rewedigital.gradle.galen

import com.rewedigital.gradle.galen.tasks.DockerComposeKillTask
import com.rewedigital.gradle.galen.tasks.DockerComposeSetupTask
import com.rewedigital.gradle.galen.tasks.DockerComposeUpTask
import com.rewedigital.gradle.galen.tasks.GalenDownloadTask
import com.rewedigital.gradle.galen.tasks.GalenExtractTask
import com.rewedigital.gradle.galen.tasks.GalenRunTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class GalenPlugin implements Plugin<Project> {

    private static final String GROUP = 'Galen'

    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, GalenPluginExtension)

        def downloadTask = createTask('galenDownload', GalenDownloadTask, 'Downloads the Galen framework', project)
        def extractTask = createTask('galenExtract', GalenExtractTask, 'Extracts the Galen framework', project)
        def runTask = createTask('galenRun', GalenRunTask, 'Executes Galen UI tests', project)
        def dockerComposeSetupTask = createTask('galenComposeSetup', DockerComposeSetupTask, "Writes the compose override file", project)
        def dockerComposeUpTask = createTask('galenComposeUp', DockerComposeUpTask, 'Creates and starts the SUT, dependent systems, and the browsers', project)
        def dockerComposeKillTask = createTask('galenComposeKill', DockerComposeKillTask, 'Kills and removes containers of the SUT, dependent systems, and the browsers', project)

        downloadTask.finalizedBy(extractTask)
        dockerComposeUpTask.dependsOn(dockerComposeSetupTask)
        dockerComposeKillTask.dependsOn(dockerComposeSetupTask)
        runTask.dependsOn(downloadTask, dockerComposeUpTask)
        runTask.finalizedBy(dockerComposeKillTask)
    }

    private <T extends Task> Task createTask(String name, Class<T> taskClass, String description, Project project) {
        Task task = project.tasks.create(name, taskClass)
        task.description = description
        task.group = GROUP
        task
    }
}
