package com.few.api.web.controller.helper

fun String.toIdentifier(): String {
    return this + "Api"
}

fun String.toApiDescription(): String {
    return this + "Description"
}

fun String.toSummary(): String {
    return this + "Summary"
}

fun String.toRequestSchema(): String {
    return this + "RequestSchema"
}

fun String.toResponseSchema(): String {
    return this + "ResponseSchema"
}

class ApiDefinitionExtension