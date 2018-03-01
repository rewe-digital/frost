package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class GalenDownloadTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(GalenDownloadTask.class)

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
            LOG.quiet("Downloading Galen Framework to '{}' ...", getDownloadFile().getAbsolutePath())
            Util.download(GALEN_DOWNLOAD_URL, getDownloadFile())
            LOG.quiet("DONE.")
        } catch (Exception e) {
            if (project.extensions[EXTENSION_NAME].failBuildOnErrors) {
                LOG.error("Encountered error: {}", e.message)
                throw e
            } else {
                LOG.warn("Encountered error, nevertheless not failing the Build: {}", e.message)
            }
        }
    }
}
