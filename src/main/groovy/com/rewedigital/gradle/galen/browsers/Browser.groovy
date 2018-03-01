package com.rewedigital.gradle.galen.browsers

enum Browser {

    CHROME('selenium/standalone-chrome', 4444),
    FIREFOX('selenium/standalone-firefox', 4444)

    public final String browserId
    public final String imageName
    public final int port

    Browser(String imageName, int port) {
        this.browserId = this.name().toLowerCase()
        this.imageName = imageName
        this.port = port
    }
}
