package org.rewedigital.frost

import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginExtensionSpec extends Specification {

    TemporaryFolder testProjectDir

    File buildFile

    def setup() {
        testProjectDir = new TemporaryFolder()
        testProjectDir.create()
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rewedigital.frost'
            }
            frost {
                galenVersion = "test-version"
                frostCacheDirectory = "${testProjectDir.newFolder()}"
                browsers = [ "chrome" ]
            }
        """
    }


    def "Adding a new Browser works"() {
        given:
        buildFile << """
            frost {
                browserImages = [ chrome: 'selenium/standalone-chrome:3.13.0' ]
            }
        """

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('frostSetup')
                .withPluginClasspath()
                .build()

        File output = new File(testProjectDir.getRoot(), 'build/tmp/docker-compose.override.frost.yml')

        then:
        output.text.contains('image: selenium/standalone-chrome:3.13.0')
    }

    def "Relative paths are relative from the project directory"() {
        given:
        def testSuitesDirectory = 'src/uiTest/frost/tests'
        def proxyConfigurationDirectory = 'src/uiTest/frost/proxy-config'
        def composeFile = 'src/uiTest/frost/docker-compose.frost.yml'
        def composeOverrideFile = 'docker-compose.override.frost.yml'
        def expectedTestSuitesDirectory = "${testProjectDir.root.absolutePath}/${testSuitesDirectory}"
        def expectedProxyConfigurationDirectory = "${testProjectDir.root.absolutePath}/${proxyConfigurationDirectory}"
        def expectedComposePath = "${testProjectDir.root.absolutePath}/${composeFile}"
        def expectedComposeOverridePath = "${testProjectDir.root.absolutePath}/build/tmp/${composeOverrideFile}"

        buildFile << """
            frost {
                testSuitesDirectory = '${testSuitesDirectory}'
                proxyConfigurationDirectory = '${proxyConfigurationDirectory}'
                composeFile = '${composeFile}'
                composeOverrideFile =  '${composeOverrideFile}'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('frostSetup', '--info')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains(expectedTestSuitesDirectory)
        result.output.contains(expectedProxyConfigurationDirectory)
        result.output.contains(expectedComposePath)
        result.output.contains(expectedComposeOverridePath)
    }
}
