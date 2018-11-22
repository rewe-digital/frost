package org.rewedigital.frost.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class GalenExtractTask extends DefaultTask {

    @InputFile
    def getDownloadFile() {
        new File(Util.cacheDirectory(project), "galen/${project.extensions[EXTENSION_NAME].galenVersion}/galen.zip")
    }

    @OutputDirectory
    def getExtractedFile() {
        new File(Util.workingDirectory(project), 'galen')
    }

    @TaskAction
    def action() {
        project.copy {
            setIncludeEmptyDirs(false)
            from project.zipTree(getDownloadFile())
            into getExtractedFile().parentFile

            eachFile { details ->
                def targetPath = details.path.replace("galen-bin-${project.extensions[EXTENSION_NAME].galenVersion}", 'galen')
                details.path = targetPath
            }
        }
    }
}
