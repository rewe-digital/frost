package com.rewedigital.gradle.galen

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class GalenPluginSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()


    def "Applying the plugin works"() {
        given:
        testProjectDir.newFile('my-build.gradle') << """
            plugins {
                id 'com.rewedigital.galen'
            }
            """

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(["-b", "my-build.gradle"])
                .withPluginClasspath()
                .build()

        then:
        notThrown Exception
    }
}
