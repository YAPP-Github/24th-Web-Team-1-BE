package com.few.crm.email.usecase.dto

class PostTemplateUseCaseDto

data class PostTemplateUseCaseIn(
    val id: String? = null,
    val templateName: String,
    val subject: String? = null,
    val version: Float? = null,
    val body: String,
    val variables: List<String> = emptyList(),
)

data class PostTemplateUseCaseOut(
    val id: String?,
    val templateName: String,
    val version: Float?,
)