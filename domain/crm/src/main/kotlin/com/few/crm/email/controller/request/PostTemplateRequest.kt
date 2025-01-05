package com.few.crm.email.controller.request

data class PostTemplateRequest(
    val id: Long? = null,
    val templateName: String,
    val subject: String? = null,
    val version: Float? = null,
    val body: String,
    val variables: List<String>? = emptyList(),
)