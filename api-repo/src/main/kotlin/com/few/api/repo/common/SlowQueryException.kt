package com.few.api.repo.common

class SlowQueryException : RuntimeException {
    var slowQuery: String? = null

    constructor(message: String, slowQuery: String) : super(message) {
        this.slowQuery = slowQuery
    }

    constructor(message: String, slowQuery: String, cause: Throwable) : super(message, cause) {
        this.slowQuery = slowQuery
    }
}