package com.rewedigital.gradle.galen

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.rewedigital.gradle.galen.util.FreePortFinder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.*

class GalenDownloadTaskSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    static final int wireMockPort = FreePortFinder.freePort
    static final WireMockServer wireMockServer  = new WireMockServer(wireMockPort)

    static {
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
                id 'com.rewedigital.galen'  
            }
            galen {
                galenVersion = "test-version"
                galenCacheDirectory = "${testProjectDir.newFolder()}"
            }
        """
    }

    def "Can Successfully Download Galen"() {
        given:
        def taskName = 'galenDownload'
        buildFile << """
            galen {
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/galen/galen.zip"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("${taskName}",  "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        result.output.contains("DONE.")
        result.task(":${taskName}").outcome == SUCCESS
    }

    def "Build Fails When Galen Download Fails"() {
        given:
        def taskName = 'galenDownload'
        buildFile << """
            galen {
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/fail/galen.zip"
            }
        """
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("${taskName}",  "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        thrown(Exception)
    }

    def "Build Fails Not When Galen Download Fails If failBuildOnErrors=false"() {
        given:
        def taskName = 'galenDownload'
        buildFile << """
            galen {
                failBuildOnErrors = false
                galenDownloadUrl = "http://127.0.0.1:${wireMockPort}/fail/galen.zip"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("${taskName}",  "-x", "galenExtract")
                .withPluginClasspath()
                .build()

        then:
        result.task(":${taskName}").outcome == SUCCESS
    }
}
