package com.rewedigital.gradle.galen

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class GalenPluginSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile


    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }


    def "Apply the plugin"() {
        given:
        buildFile << """
            plugins {
                id 'com.rewedigital.galen'
            }
            """

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .build()

        then:
        notThrown Exception
    }
}
