package org.rewedigital.frost

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginExtensionSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'org.rewedigital.frost'
            }
            galen {
                galenVersion = "test-version"
                galenCacheDirectory = "${testProjectDir.newFolder()}"
                browsers = [ "chrome" ]
            }
        """
    }

    def "Adding a new Browser works"() {
        given:
        buildFile << """
            galen {
                browserImages = [ chrome: 'selenium/standalone-chrome:3.13.0' ]
            }
        """

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('galenSetup')
                .withPluginClasspath()
                .build()

        File output = new File(testProjectDir.getRoot(), 'build/tmp/docker-compose.override.galen.yml')

        then:
        output.text.contains('image: selenium/standalone-chrome:3.13.0')
    }

    def "Relative paths are relative from the project directory"() {
        given:
        def testSuitesDirectory = 'src/uiTest/galen/tests'
        def proxyConfigurationDirectory = 'src/uiTest/galen/proxy-config'
        def composeFile = 'src/uiTest/galen/docker-compose.galen.yml'
        def composeOverrideFile = 'docker-compose.override.galen.yml'
        def expectedTestSuitesDirectory = "${testProjectDir.root.absolutePath}/${testSuitesDirectory}"
        def expectedProxyConfigurationDirectory = "${testProjectDir.root.absolutePath}/${proxyConfigurationDirectory}"
        def expectedComposePath = "${testProjectDir.root.absolutePath}/${composeFile}"
        def expectedComposeOverridePath = "${testProjectDir.root.absolutePath}/build/tmp/${composeOverrideFile}"

        buildFile << """
            galen {
                testsuitesDirectory = '${testSuitesDirectory}'
                proxyConfigurationDirectory = '${proxyConfigurationDirectory}'
                composeFile = '${composeFile}'
                composeOverrideFile =  '${composeOverrideFile}'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('galenSetup', '--info')
                .withPluginClasspath()
                .build()

        then:
        result.output.contains(expectedTestSuitesDirectory)
        result.output.contains(expectedProxyConfigurationDirectory)
        result.output.contains(expectedComposePath)
        result.output.contains(expectedComposeOverridePath)
    }
}
