package com.rewedigital.gradle.galen.tasks

import com.rewedigital.gradle.galen.browsers.Browser
import com.rewedigital.gradle.galen.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml

import static com.rewedigital.gradle.galen.GalenPluginExtension.EXTENSION_NAME
import static java.util.concurrent.TimeUnit.HOURS
import static java.util.concurrent.TimeUnit.MINUTES

class GalenRunTask extends DefaultTask {

    static final Logger LOG = Logging.getLogger(GalenRunTask.class)

    private static final int TESTSUITE_TIMEOUT_MILLIS = HOURS.toMillis(1)


    @TaskAction
    def action() {
        def workingDirectory = new File(project.getRootDir(), project.extensions[EXTENSION_NAME].galenWorkingDirectory)
        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            def errorMessage = "Error creating galen working directory ${project.extensions[EXTENSION_NAME].galenWorkingDirectory}."
            if (project.extensions[EXTENSION_NAME].failBuildOnErrors) {
                LOG.quiet(errorMessage)
                throw new GradleException(errorMessage)
            } else {
                LOG.warn(errorMessage)
            }
        }

        File composeOverride = Util.composeOverrideFile(project)
        def ports = getPortsFromCompose(composeOverride)

        waitUntilServiceIsReady(ports[targetHost()])

        try {
            executeTestSuites(ports)
        } catch (Exception e) {
            def errorMessage = "Test execution failed."
            if (project.extensions[EXTENSION_NAME].failBuildOnErrors) {
                LOG.quiet(errorMessage)
                throw new GradleException(errorMessage, e)
            } else {
                LOG.warn(errorMessage)
            }
        }
    }


    private String targetHost() {
        project.extensions[EXTENSION_NAME].useProxy ? 'proxy' : 'sut'
    }

    void executeTestSuites(browserAndPorts) {
        def testGroups = project.extensions[EXTENSION_NAME].testGroups
        def testsuitesDirectory = project.extensions[EXTENSION_NAME].testsuitesDirectory

        def testsDescription = testGroups ? "test groups: '${testGroups}'" : 'ALL tests'
        LOG.info("Executing Test Suites ({}) ...", testsDescription)
        def testPath = new File(testsuitesDirectory).absolutePath
        Exception lastException = null

        def testSuiteStarters = []
        Util.getBrowsers(project).each { Browser browser ->
            def browserPort = browserAndPorts[browser.browserId]
            def seleniumDriverUrl = "http://localhost:${browserPort}/wd/hub"
            def thread = new Thread({
                try {
                    def reportDirectory = "build/reports/tests/uiTest/${browser.browserId}/${new File(testPath).name}"
                    executeTestSuitesOnSpecificBrowser(testPath, reportDirectory, seleniumDriverUrl, browser.browserId, testGroups)
                } catch (Exception e) {
                    lastException = e
                    LOG.warn("Test suite execution failed: {}", e.getMessage())
                }
            })
            testSuiteStarters << thread
        }

        testSuiteStarters.each { it.start() }
        testSuiteStarters.each { it.join(TESTSUITE_TIMEOUT_MILLIS) }

        if (lastException != null) {
            throw lastException
        }
    }

    void executeTestSuitesOnSpecificBrowser(testPath, reportsDirectory, seleniumDriverUrl = null, browser, testGroups) {
        def workingDirectory = new File(project.getRootDir(), project.extensions[EXTENSION_NAME].galenWorkingDirectory)
        def numberOfParallelTests = project.extensions[EXTENSION_NAME].numberOfParallelTests

        def cmd = ["${workingDirectory}/galen/galen", "test", "${testPath}",
                   "--htmlreport", "${reportsDirectory}",
                   "--junitreport", "build/test-results/uiTest/TEST-${browser}.xml",
                   "--parallel-tests", "${numberOfParallelTests}",
                   "-Dgalen.settings.website=http://${targetHost()}:8080"]

        if (testGroups != null) {
            cmd << "--groups"
            cmd << "${testGroups}"
        }

        if (seleniumDriverUrl != null) {
            cmd << "-Dgalen.browserFactory.selenium.grid.browser=${browser}"
            cmd << "-Dgalen.browserFactory.selenium.runInGrid=true"
            cmd << "-Dgalen.browserFactory.selenium.grid.url=${seleniumDriverUrl}"
        } else {
            cmd << "-Dgalen.default.browser=${browser}"
        }

        Util.executeSynchronously(cmd, "test__${browser}")
    }

    private void waitUntilServiceIsReady(port) {
        def healthCheckPathWithoutLeadingSlash = project.extensions[EXTENSION_NAME].sutHealthCheckEndpointPath.replaceAll("^/", "")
        def url = new URL("http://localhost:${port}/${healthCheckPathWithoutLeadingSlash}")
        def endTimeMillis = System.currentTimeMillis() + MINUTES.toMillis(project.extensions[EXTENSION_NAME].sutReadinessTimeoutInMinutes)

        LOG.quiet("Healthcheck on URL '{}' ...", url)
        while (System.currentTimeMillis() < endTimeMillis) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection()
                connection.setRequestMethod('GET')
                connection.connect()

                int code = connection.responseCode
                if (code == 200) {
                    LOG.quiet('SUCCESSFUL.')
                    return
                }
            } catch (IOException e) {
                // ignore
            }
            sleep(1000)
        }
        LOG.warn('FAILED.')
        throw new GradleException('Service was not healthy')
    }

    def getPortsFromCompose(composeOverrideFile) {
        def ports = [:]
        def compose = new Yaml().load(composeOverrideFile.text)
        compose.each { entry ->
            if (entry.toString().startsWith('services')) {
                entry.getValue().each { service ->
                    service.getValue().each() { config ->
                        if (config.getKey().equals('ports')) {
                            ports.put(service.getKey(), config.getValue().get(0).toString().split(':')[0])
                        }
                    }
                }
            }
        }
        ports
    }
}
