package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.browsers.Browser
import com.rewedigital.gradle.galen.util.FreePortFinder
import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME

class DockerComposeSetupTask extends DefaultTask {

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
        def content = "version: '2'\n" +
                "services:\n"

        if (useProxy()) {
            def absoluteProxyConfigurationDirectory = new File(project.extensions[EXTENSION_NAME].proxyConfigurationDirectory).getAbsolutePath()
            content += "  proxy:\n" +
                    "    image: rodolpheche/wiremock\n" +
                    "    ports:\n" +
                    "      - ${FreePortFinder.freePort}:8080\n" +
                    "    volumes:\n" +
                    "      - ${absoluteProxyConfigurationDirectory}:/home/wiremock\n" +
                    "    command: [\"--verbose\"]\n"
        } else {
            content += "  sut:\n" +
                    "    ports:\n" +
                    "      - ${FreePortFinder.freePort}:8080\n"
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
