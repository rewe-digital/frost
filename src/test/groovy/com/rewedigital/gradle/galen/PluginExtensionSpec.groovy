package com.rewedigital.gradle.galen

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
                id 'com.rewedigital.galen'
            }
            galen {
                galenVersion = "test-version"
                galenCacheDirectory = "${testProjectDir.newFolder()}"
                browsers = [ "chrome" ]
            }
        """
    }

    void "Adding a new Browser works"() {
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
        println output.text

        then:
        output.text.contains('image: selenium/standalone-chrome:3.13.0')
    }
}
