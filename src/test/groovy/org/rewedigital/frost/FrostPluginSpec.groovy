package org.rewedigital.frost

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class FrostPluginSpec extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Unroll
    def "Applying the plugin works with Gradle version #gradleVersion"() {
        given:
        testProjectDir.newFile('my-build.gradle') << """
            plugins {
                id 'org.rewedigital.frost'
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
        gradleVersion << ['4.8', '4.9', '4.10']
    }
}
