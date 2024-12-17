package web.helper

fun String.toIdentifier(): String = this + "Api"

fun String.toApiDescription(): String = this + "Description"

fun String.toSummary(): String = this + "Summary"

fun String.toRequestSchema(): String = this + "RequestSchema"

fun String.toResponseSchema(): String = this + "ResponseSchema"

class ApiDefinitionExtension