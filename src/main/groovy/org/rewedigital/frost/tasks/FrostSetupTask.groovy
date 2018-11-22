package org.rewedigital.frost.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.rewedigital.frost.browsers.Browser
import org.rewedigital.frost.util.FreePortFinder
import org.rewedigital.frost.util.Util

import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class FrostSetupTask extends DefaultTask {

    static final Logger LOG = Logging.getLogger(FrostSetupTask.class)

    @OutputFile
    def getComposeOverrideFile() {
        Util.composeOverrideFile(project)
    }

    @Input
    def getBrowsers() {
        project.extensions[EXTENSION_NAME].browsers
    }

    @Input
    def useProxy() {
        project.extensions[EXTENSION_NAME].useProxy
    }


    @TaskAction
    def action() {
        LOG.info("Working directory: ${Util.workingDirectory(project).absolutePath}")
        LOG.info("Docker-compose file: ${Util.composeFile(project).absolutePath}")
        LOG.info("Docker-compose override file: ${Util.composeOverrideFile(project).absolutePath}")
        LOG.info("Proxy configuration directory: ${Util.proxyConfigurationDirectory(project).absolutePath}")
        LOG.info("Test suites directory: ${Util.testSuitesDirectory(project).absolutePath}")

        def content = "version: '2'\n" +
                "services:\n"
        if (useProxy()) {
            def absoluteProxyConfigurationDirectory = Util.proxyConfigurationDirectory(project).absolutePath
            content += "  proxy:\n" +
                    "    image: rodolpheche/wiremock\n" +
                    "    ports:\n" +
                    "      - ${FreePortFinder.freePort}:${project.extensions[EXTENSION_NAME].sutPort}\n" +
                    "    volumes:\n" +
                    "      - ${absoluteProxyConfigurationDirectory}:/home/wiremock\n" +
                    "    command: [\"--verbose\"]\n"
        } else {
            content += "  sut:\n" +
                    "    ports:\n" +
                    "      - ${FreePortFinder.freePort}:${project.extensions[EXTENSION_NAME].sutPort}\n"
        }
        Util.getBrowsers(project).each() { Browser browser ->
            content += "  ${browser.browserId}:\n" +
                    "    image: ${browser.imageName}\n" +
                    "    ports:\n" +
                    "      - ${FreePortFinder.freePort}:${browser.port}\n"
        }
        getComposeOverrideFile().write(content)
    }
}
