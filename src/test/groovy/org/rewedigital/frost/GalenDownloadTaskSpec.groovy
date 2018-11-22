package org.rewedigital.frost

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.rewedigital.frost.util.FreePortFinder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GalenDownloadTaskSpec extends Specification {

    static final String TASK_NAME = ':galenDownload'

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    static final int wireMockPort = FreePortFinder.freePort
    static final WireMockServer wireMockServer = new WireMockServer(wireMockPort)
    File buildFile


    def setupSpec() {
        wireMockServer.stubFor(WireMock.any(WireMock.urlPathEqualTo("/galen/galen.zip"))
                .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/octet-stream")
                .withBodyFile("galen.zip")))
        wireMockServer.start()
    }

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rewedigital.frost'
            }
            galen {
                galenVersion = "test-version"
                galenCacheDirectory = "${testProjectDir.newFolder()}"
            }
        """
    }


    def "Can Successfully Download Galen"() {
        given:
        buildFile << """
            galen {
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/galen/galen.zip"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(TASK_NAME, "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        result.output.contains("DONE.")
        result.task(TASK_NAME).outcome == SUCCESS
    }

    def "Build Fails When Galen Download Fails"() {
        given:
        buildFile << """
            galen {
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/fail/galen.zip"
            }
        """
        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(TASK_NAME, "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        thrown(Exception)
    }

    def "Build Does Not Fail When Galen Download Fails If failBuildOnErrors=false"() {
        given:
        buildFile << """
            galen {
                failBuildOnErrors = false
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/fail/galen.zip"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(TASK_NAME, "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        result.task(TASK_NAME).outcome == SUCCESS
    }
}
