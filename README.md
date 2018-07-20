## Galen Gradle Plugin
This is a Gradle Plugin that enables you to run self-contained, multi-browser Galen based Selenium tests within your build. External dependencies(e.g. Databases, Asset Servers, ...) are provided as Docker containers and can be defined via a simple docker-compose file.

### Getting Started
The plugin is available via Artifactory.
```
buildscript {
    repositories {
        maven { url "https://mvn.mycompany.com/repository-foo" }
    }
    dependencies {
        classpath('com.rewedigital:gradle-galen-plugin:2.12')
    }
}
apply plugin: "com.rewedigital.galen"
```
You can use the following configuration in your build.gradle file:
```
galen {
    // Galen working directory, default is 'galen'
    galenWorkingDirectory = "uiTest"
    
    // Directory in which to store the cached Galen binary, default is '<USER_HOME>/.galen'
    galenCacheDirectory = "uiTest"

    // Which browsers to use, default is ['chrome', 'firefox']. Supported browsers are 'chrome' and 'firefox'.
    browsers = ["chrome"] 
    
    // Directory containing the Galen test suites, default is 'src/uiTest/galen/tests'
    testsuitesDirectory = "src/uiTest/galen/tests"
    
    // Comma separated list of test groups to be executed, default is all test groups. If left empty all test groups are executed.
    testGroups = "ci"
   
    // Amount of threads per browser for running tests in parallel, default is 1
    numberOfParallelTests = 2

    // Tag of the SUT image to be tested, default is 'latest'.
    sutTag = "${applicationVersion}"
    
    // Path of the endpoint to be queried in order to detect if the SUT is up and running, default is '/admin/healthcheck'.
    // The endpoint must respond with status code 200 if and only if the SUT is ready.
    sutHealthCheckEndpointPath = "/health"
   
    // The maximum time to wait (in minutes) for the SUT healthcheck to signal UP after service start, default is 5.
    sutReadinessTimeoutInMinutes = 10

    // Docker compose file describing the environment of the SUT including all of its dependencies, default is 'docker-compose.yml'. 
    // You should omit ports, s.t. the plugin will chose a random free port.
    composeFile = 'docker-compose.galen.yml'

    // Docker compose file to describe the environment of the browsers. Default is 'docker-compose.override.galen.yml'.
    // There is no need to manage this manually, it is just for internal use.
    composeOverrideFile = 'docker-compose.override.galen.yml'
    
    // Whether the requests to the SUT should be routed through a proxy (wiremock), default is false.
    // This can be useful to add or modify HTTP request headers that your SUT may rely on, as Galen does not seem to support this directly. 
    useProxy = true
    
    // When using the proxy, this is the directory where the wiremock configuration files are based. Default is 'galen'.
    proxyConfigurationDirectory = 'uiTest/wiremock-config'

    // Whether or not failing Galen tests or framework errors should let the Gradle task/build fail, default is true.
    failBuildOnErrors = false
}
```
#### Example Compose File
To define the external dependencies of your SUT(System Under Test) use a docker-compose file like this:
```
version: '2'

services:
  db:
    image: docker-registry.mycompany.com/myservice-test-db:latest
  assets:
    image: sebp/lighttpd
    volumes:
      - ./npm_dist:/var/www/localhost/htdocs
  sut:
    depends_on:
      - "db"
      - "assets"
    image: docker-registry.mycompany.com/myservice:${TAG}
    command: ["-c", "/opt/wait-for.sh db:5432 && /usr/sbin/java-service start"]
    entrypoint: ["/bin/sh"]
    environment:
        - SYSTEM_MEMORY=1024
        - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/
        - SPRING_DATASOURCE_USERNAME=postgres
        - SPRING_DATASOURCE_PASSWORD=
        - ENVIRONMENT_ASSETBASEURL=http://assets:80/
```

### TODO
- Test coverage
- Circle CI build

### License
MIT