package com.rewedigital.gradle.galen

class GalenPluginExtension {

    static final String EXTENSION_NAME = 'galen'


    String galenWorkingDirectory = 'galen'
    String galenCacheDirectory = "${System.getProperty('user.home')}/.galen"
    String galenVersion = '2.3.7'

    String[] browsers = ['firefox', 'chrome']
    HashMap<String, String> browserImages = new HashMap<>()
    String testsuitesDirectory = 'src/uiTest/galen/tests'
    boolean recursive = false
    String testGroups
    int numberOfParallelTests = 1

    String sutTag = 'latest'
    int sutPort = 8080
    String sutHealthCheckEndpointPath = '/admin/healthcheck'
    int sutReadinessTimeoutInMinutes = 5

    String composeFile = 'docker-compose.yml'
    String composeOverrideFile = 'docker-compose.override.galen.yml'

    boolean useProxy = false
    String proxyConfigurationDirectory = 'galen'

    boolean failBuildOnErrors = true


    String getGalenDownloadUrl() {
        "https://github.com/galenframework/galen/releases/download/galen-${galenVersion}/galen-bin-${galenVersion}.zip"
    }
}
