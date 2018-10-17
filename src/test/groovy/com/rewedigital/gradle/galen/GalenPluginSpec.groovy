package com.rewedigital.gradle.galen

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class GalenPluginSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Unroll
    def "Applying the plugin works with Gradle version #gradleVersion"() {
        given:
        testProjectDir.newFile('my-build.gradle') << """
            plugins {
                id 'com.rewedigital.galen'
            }
            """

        when:
        GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments(["-b", "my-build.gradle"])
                .withPluginClasspath()
                .build()

        then:
        notThrown Exception

        where:
        gradleVersion << ['4.3', '4.4', '4.5', '4.6', '4.7', '4.8', '4.9', '4.10']

    }
}
