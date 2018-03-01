#!/bin/groovy

node {
    stage('Build and Test') {
        timeout(time: 2, unit: 'MINUTES') {
            checkout scm
            sh "./gradlew clean build"
        }
    }
    stage('Clean') {
        deleteDir()
    }
}