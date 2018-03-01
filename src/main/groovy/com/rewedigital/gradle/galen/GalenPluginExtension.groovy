package com.rewedigital.gradle.galen

class GalenPluginExtension {

    final static String EXTENSION_NAME = 'galen'


    String galenWorkingDirectory = 'galen'
    String galenCacheDirectory = "${System.getProperty('user.home')}/.galen"

    String[] browsers = ['chrome', 'firefox']
    String testsuitesDirectory = 'src/uiTest/galen/tests'
    String testGroups
    int numberOfParallelTests = 1

    String sutTag = 'latest'
    String sutHealthCheckEndpointPath = '/admin/healthcheck'
    int sutReadinessTimeoutInMinutes = 5

    String composeFile = 'docker-compose.yml'
    String composeOverrideFile = 'docker-compose.override.galen.yml'

    boolean useProxy = false
    String proxyConfigurationDirectory = 'galen'

    boolean failBuildOnErrors = true
}
