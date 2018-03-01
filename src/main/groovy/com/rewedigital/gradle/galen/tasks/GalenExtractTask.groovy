package com.rewedigital.gradle.galen.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class GalenExtractTask extends DefaultTask {

    @InputFile
    def getDownloadFile() {
        new File(project.extensions[EXTENSION_NAME].galenCacheDirectory, "/${GalenDownloadTask.GALEN_RELEASE}/galen.zip")
    }

    @OutputDirectory
    def getExtractedFile() {
        new File(project.extensions[EXTENSION_NAME].galenWorkingDirectory, 'galen')
    }

    @TaskAction
    def action() {
        project.copy {
            setIncludeEmptyDirs(false)
            from project.zipTree(getDownloadFile())
            into project.extensions[EXTENSION_NAME].galenWorkingDirectory

            eachFile { details ->
                def targetPath = details.path.replace("galen-bin-${GalenDownloadTask.GALEN_RELEASE}", 'galen')
                details.path = targetPath
            }
        }
    }
}
