## FROST

_Making GUI system tests extremely cool!_
  
FROST enables you to run self-contained, multi-browser Galen-based Selenium tests within your build. External dependencies(e.g. Databases, Asset Servers etc.) are provided as Docker containers and can be defined just via a docker-compose file.

FROST is implemented as a Gradle Plugin. But this does not require your project being a Gradle project. It simple does not matter at all how your project is being built.


### Getting Started
The plugin is available via [Gradle Plugin Portal](https://plugins.gradle.org/). Just apply it in your `build.gradle` as follows:
```
buildscript {
    dependencies {
        classpath('org.rewedigital:frost:0.1')
    }
}
apply plugin: "org.rewedigital.frost"
```
You can use the following configuration in your build.gradle file:
```
frost {
    // FROST working directory, default is 'frost'
    frostWorkingDirectory = "uiTest"
    
    // Directory in which to store the cached Galen binary, default is '<USER_HOME>/.frost'
    frostCacheDirectory = "uiTest"
    
    // The Galen version to use, default is "2.4.0".
    galenVersion = '2.4.0'
    
    // The URL where to download the Galen binary, default is "https://github.com/galenframework/galen/releases/download/galen-${galenVersion}/galen-bin-${galenVersion}.zip".
    galenDownloadUrl = "https://my-company-bin-repository/galen/galen-special-version.zip"

    // Which browsers to use, default is ['chrome', 'firefox']. Supported browsers are 'chrome' and 'firefox'.
    browsers = ["chrome"]
    
    // Which Docker images to use for the browsers, default is selenium/standalone-chrome:latest and selenium/standalone-firefox:latest.
    browserImages = [ chrome: 'selenium/standalone-chrome:3.13.0']
    
    // Directory containing the Galen test suites, default is 'src/uiTest/frost/tests'
    testsuitesDirectory = "src/uiTest/frost/tests"
    
    // Whether to search for all ".test" files recursively in the "testsuitesDirectory", default is false.
    recursive = true

    // Comma separated list of test groups to be executed, default is all test groups. If left empty all test groups are executed.
    testGroups = "ci"
   
    // Amount of threads per browser for running tests in parallel, default is 1
    numberOfParallelTests = 2

    // Tag of the SUT image to be tested, default is 'latest'.
    sutTag = "${applicationVersion}"
    
    // Port (internal) used by the SUT, default is 8080.
    sutPort = 8081
    
    // Path of the endpoint to be queried in order to detect if the SUT is up and running, default is '/admin/healthcheck'.
    // The endpoint must respond with status code 200 if and only if the SUT is ready.
    sutHealthCheckEndpointPath = "/health"
   
    // The maximum time to wait (in minutes) for the SUT healthcheck to signal UP after service start, default is 5.
    sutReadinessTimeoutInMinutes = 10

    // Docker compose file describing the environment of the SUT including all of its dependencies, default is 'docker-compose.yml'. 
    // You should omit ports, s.t. the plugin will chose a random free port.
    composeFile = 'docker-compose.frost.yml'

    // Docker compose file to describe the environment of the browsers. Default is 'docker-compose.override.frost.yml'.
    // There is no need to manage this manually, it is just for internal use.
    composeOverrideFile = 'docker-compose.override.frost.yml'
    
    // Whether the requests to the SUT should be routed through a proxy (wiremock), default is false.
    // This can be useful to add or modify HTTP request headers that your SUT may rely on, as Galen does not seem to support this directly. 
    useProxy = true
    
    // When using the proxy, this is the directory where the wiremock configuration files are based. Default is 'frost'.
    proxyConfigurationDirectory = 'uiTest/wiremock-config'

    // Whether or not failing Frost tests or framework errors should let the Gradle task/build fail, default is true.
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


#### Example Run
To run simply execute:
```
./gradlew frostRun
```


### License
The MIT license (MIT)

Copyright (c) 2017-2018 REWE Digital GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
