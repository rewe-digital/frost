package com.rewedigital.gradle.galen.util

import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class FrontendTestSetupSpec extends Specification {

    def "Pull existing image"() {
        when:
        FrontendTestSetup.pullDockerImage("docker-registry.rewe-digital.com:5000/rewe-theme-service:master", "", true)

        then:
        true
    }



}
