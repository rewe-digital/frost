package com.rewedigital.gradle.galen.util

import com.rewedigital.gradle.galen.browsers.Browser
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME
import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class Util {

    private static final Logger LOG = Logging.getLogger(Util.class)

    private static final long SYNCHRONOUS_PROCESS_TERMINATION_TIMEOUT_MINUTES = 15


    static def composeOverrideFile(project) {
        new File("${project.buildDir}", 'tmp/' + "${project.extensions[EXTENSION_NAME].composeOverrideFile}")
    }

    static def composeFile(project) {
        new File("${project.projectDir}", "${project.extensions[EXTENSION_NAME].composeFile}")
    }

    static download(url, target) {
        def file = new FileOutputStream(target)
        def out = new BufferedOutputStream(file)
        out << new URL(url).openStream()
        out.close()
    }

    static Browser[] getBrowsers(project) {
        def browser = []
        project.extensions[EXTENSION_NAME].browsers.each() { String browserName ->
            try {
                browser << Browser.valueOf(browserName.toUpperCase())
            } catch (IllegalArgumentException e) {
                throw new GradleException("ERROR: Unknown browser '${browserName}' given. Aborting.")
            }
        }
        browser
    }

    static int executeSynchronously(cmd, processIdentifier) {
        return executeInternal(cmd, true, processIdentifier)
    }

    static int executeAsynchronously(cmd, processIdentifier, failFast = true) {
        return executeInternal(cmd, false, processIdentifier, failFast)
    }

    private static int executeInternal(cmd, executeSynchronously, processIdentifier, failFast = true) {
        LOG.info("Executing '{}'", cmd.join(" "))
        def logDirectory = new File('build/reports/galen-logs')
        def logFile = new File(logDirectory, "${processIdentifier}.log")
        def processBuilder = new ProcessBuilder(cmd as String[])
        if (logFile != null) {
            logFile.parentFile.mkdirs()
            processBuilder.redirectOutput(logFile)
            processBuilder.redirectError(logFile)
        }
        def process = processBuilder.start()

        if (executeSynchronously) {
            def hasExited = process.waitFor(SYNCHRONOUS_PROCESS_TERMINATION_TIMEOUT_MINUTES, MINUTES)
            if (!hasExited) {
                throw new GradleException('Timeout')
            }
            def exitCode = process.exitValue()
            if (exitCode != 0) {
                throw new GradleException("Command failed (${exitCode}): '${cmd.join(" ")}'")
            }
            return exitCode
        } else {
            // we wait at least for a little while, in order to verify that the process did not crash
            if (failFast && process.waitFor(1, SECONDS)) {
                def exitCode = process.exitValue()
                if (exitCode != 0) {
                    throw new GradleException("Command failed (${exitCode}): '${cmd.join(" ")}'")
                }
            }
            return 0
        }
    }
}
