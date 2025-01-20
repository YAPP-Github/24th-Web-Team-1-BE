package com.few.domain.generator.client.request

data class OpenAiRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val response_format: Map<String, String> = mapOf("type" to "json_object"),
)