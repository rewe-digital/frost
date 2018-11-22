package org.rewedigital.frost

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.rewedigital.frost.tasks.DockerComposeKillTask
import org.rewedigital.frost.tasks.DockerComposePullTask
import org.rewedigital.frost.tasks.DockerComposeUpTask
import org.rewedigital.frost.tasks.GalenDownloadTask
import org.rewedigital.frost.tasks.GalenExtractTask
import org.rewedigital.frost.tasks.GalenRunTask
import org.rewedigital.frost.tasks.SetupTask

import static GalenPluginExtension.EXTENSION_NAME

class GalenPlugin implements Plugin<Project> {

    private static final String GROUP = 'Galen'


    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, GalenPluginExtension)

        def downloadTask = createTask('galenDownload', GalenDownloadTask, 'Downloads the Galen framework', project)
        def extractTask = createTask('galenExtract', GalenExtractTask, 'Extracts the Galen framework', project)
        def runTask = createTask('galenRun', GalenRunTask, 'Executes Galen UI tests', project)
        def setupTask = createTask('galenSetup', SetupTask, "Writes the compose override file", project)
        def dockerComposePullTask = createTask('galenComposePull', DockerComposePullTask, 'Pulls for latest feasible versions of the SUT, dependent systems, and the browsers', project)
        def dockerComposeUpTask = createTask('galenComposeUp', DockerComposeUpTask, 'Creates and starts the SUT, dependent systems, and the browsers', project)
        def dockerComposeKillTask = createTask('galenComposeKill', DockerComposeKillTask, 'Kills and removes containers of the SUT, dependent systems, and the browsers', project)

        downloadTask.finalizedBy(extractTask)
        dockerComposePullTask.dependsOn(setupTask)
        dockerComposeKillTask.dependsOn(setupTask)
        dockerComposeUpTask.dependsOn(setupTask, dockerComposePullTask)
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
