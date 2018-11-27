package org.rewedigital.frost.util

class FrostException extends Exception {

    def failures = [:]

    FrostException(failures) {
        this.failures = failures
    }
}
