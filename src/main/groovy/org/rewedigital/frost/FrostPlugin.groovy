package org.rewedigital.frost

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.rewedigital.frost.tasks.FrostComposeKillTask
import org.rewedigital.frost.tasks.FrostComposePullTask
import org.rewedigital.frost.tasks.FrostComposeUpTask
import org.rewedigital.frost.tasks.FrostRunTask
import org.rewedigital.frost.tasks.FrostSetupTask
import org.rewedigital.frost.tasks.GalenDownloadTask
import org.rewedigital.frost.tasks.GalenExtractTask

import static FrostPluginExtension.EXTENSION_NAME

class FrostPlugin implements Plugin<Project> {

    private static final String GROUP = 'Frost'


    void apply(Project project) {
        project.extensions.create(EXTENSION_NAME, FrostPluginExtension)

        def downloadTask = createTask('galenDownload', GalenDownloadTask, 'Downloads the Galen framework', project)
        def extractTask = createTask('galenExtract', GalenExtractTask, 'Extracts the Galen framework', project)
        def runTask = createTask('frostRun', FrostRunTask, 'Executes FROST GUI tests', project)
        def setupTask = createTask('frostSetup', FrostSetupTask, "Writes the compose override file", project)
        def frostComposePullTask = createTask('frostComposePull', FrostComposePullTask, 'Pulls for latest feasible versions of the SUT, dependent systems, and the browsers', project)
        def frostComposeUpTask = createTask('frostComposeUp', FrostComposeUpTask, 'Creates and starts the SUT, dependent systems, and the browsers', project)
        def frostComposeKillTask = createTask('frostComposeKill', FrostComposeKillTask, 'Kills and removes containers of the SUT, dependent systems, and the browsers', project)

        downloadTask.finalizedBy(extractTask)
        frostComposePullTask.dependsOn(setupTask)
        frostComposeKillTask.dependsOn(setupTask)
        frostComposeUpTask.dependsOn(setupTask, frostComposePullTask)
        runTask.dependsOn(downloadTask, frostComposeUpTask)
        runTask.finalizedBy(frostComposeKillTask)
    }

    private <T extends Task> Task createTask(String name, Class<T> taskClass, String description, Project project) {
        Task task = project.tasks.create(name, taskClass)
        task.description = description
        task.group = GROUP
        task
    }
}
