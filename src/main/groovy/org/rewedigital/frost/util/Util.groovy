package org.rewedigital.frost.util

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.rewedigital.frost.browsers.Browser

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS
import static org.rewedigital.frost.FrostPluginExtension.EXTENSION_NAME

class Util {

    private static final Logger LOG = Logging.getLogger(Util.class)

    private static final long SYNCHRONOUS_PROCESS_TERMINATION_TIMEOUT_MINUTES = 15


    static File workingDirectory(Project project) {
        def frostWorkingDirectory = new File(project.extensions[EXTENSION_NAME].frostWorkingDirectory)
        makeAbsoluteIfIsRelative(frostWorkingDirectory, project.rootDir)
    }

    static File composeOverrideFile(Project project) {
        def composeOverrideFile = new File(project.extensions[EXTENSION_NAME].composeOverrideFile)
        makeAbsoluteIfIsRelative(composeOverrideFile, new File(project.buildDir, "tmp"))
    }

    static File composeFile(Project project) {
        def composeFile = new File(project.extensions[EXTENSION_NAME].composeFile)
        makeAbsoluteIfIsRelative(composeFile, project.projectDir)
    }

    static File proxyConfigurationDirectory(Project project) {
        def proxyConfigurationDirectory = new File(project.extensions[EXTENSION_NAME].proxyConfigurationDirectory)
        makeAbsoluteIfIsRelative(proxyConfigurationDirectory, project.projectDir)
    }

    static File testSuitesDirectory(Project project) {
        def testSuitesDirectory = new File(project.extensions[EXTENSION_NAME].testSuitesDirectory)
        makeAbsoluteIfIsRelative(testSuitesDirectory, project.projectDir)
    }

    static File cacheDirectory(Project project) {
        def cacheDirectory = new File(project.extensions[EXTENSION_NAME].frostCacheDirectory)
        makeAbsoluteIfIsRelative(cacheDirectory, project.projectDir)
    }

    static File composeOutputDirectory(project) {
        new File(project.buildDir, "reports/frost-logs")
    }

    private static File makeAbsoluteIfIsRelative(File possiblyRelativeDirectory, File baseDirectory) {
        possiblyRelativeDirectory.isAbsolute() ?
                possiblyRelativeDirectory :
                new File(baseDirectory, possiblyRelativeDirectory.toString())
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
                Browser b = Browser.valueOf(browserName.toUpperCase())
                if (project.extensions[EXTENSION_NAME].browserImages.containsKey(browserName)) {
                    b.imageName = project.extensions[EXTENSION_NAME].browserImages.get(browserName)
                }
                browser << b

            } catch (IllegalArgumentException e) {
                throw new GradleException("ERROR: Unknown browser '${browserName}' given. Aborting.")
            }
        }
        browser
    }

    static int executeSynchronously(cmd, processIdentifier, logDirectory) {
        return executeInternal(cmd, true, processIdentifier, logDirectory)
    }

    static int executeAsynchronously(cmd, processIdentifier, logDirectory, failFast = true) {
        return executeInternal(cmd, false, processIdentifier, logDirectory, failFast)
    }

    private static int executeInternal(cmd, executeSynchronously, processIdentifier, logDirectory, failFast = true) {
        LOG.info("Executing '{}'", cmd.join(" "))
        def logFile = new File(logDirectory, "${processIdentifier}.log")
        logFile.parentFile.mkdirs()

        def processBuilder = new ProcessBuilder(cmd as String[])
        processBuilder.redirectOutput(logFile)
        processBuilder.redirectError(logFile)
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
