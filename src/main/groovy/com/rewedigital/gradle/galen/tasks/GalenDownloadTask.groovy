package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class GalenDownloadTask extends DefaultTask {

    static final String GALEN_RELEASE = '2.3.6'

    @Input
    static final String GALEN_DOWNLOAD_URL = "https://github.com/galenframework/galen/releases/download/galen-${GALEN_RELEASE}/galen-bin-${GALEN_RELEASE}.zip"

    @OutputFile
    def getDownloadFile() {
        new File(project.extensions[EXTENSION_NAME].galenCacheDirectory, "/${GALEN_RELEASE}/galen.zip")
    }

    @TaskAction
    def action() {
        try {
            print "Downloading Galen Framework to '${getDownloadFile().getAbsolutePath()}' ..."
            System.out.flush()
            Util.download(GALEN_DOWNLOAD_URL, getDownloadFile())
            println " DONE"
        } catch (Exception e) {
            if (project.extensions[EXTENSION_NAME].failBuildOnErrors) {
                throw e
            } else {
                println("\nERROR: Encountered error, nevertheless not failing the Build: ${e.message}")
            }
        }
    }
}
