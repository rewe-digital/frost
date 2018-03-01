package com.rewedigital.gradle.galen.util

import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class UtilSpec extends Specification {

    def "Download file"() {
        given:
        def file = File.createTempFile("UtilSpec", "downloadTest")
        if (file.exists()) {
            file.delete()
        }

        when:
        Util.download("https://artifactory.rewe-digital.com/artifactory/jcenter-cache/commons-lang/commons-lang/1.0/commons-lang-1.0.jar", file.getAbsolutePath())

        then:
        file.exists()
    }
}
