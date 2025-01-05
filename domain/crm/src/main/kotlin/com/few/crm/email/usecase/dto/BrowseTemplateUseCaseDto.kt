package com.few.crm.email.usecase.dto

data class BrowseTemplateUseCaseDto(
    val withHistory: Boolean,
)

data class BrowseTemplateUseCaseIn(
    val withHistory: Boolean,
)

data class BrowseTemplateUseCaseOut(
    val result: List<TemplateResult>,
)

data class TemplateResult(
    val template: TemplateCurrent,
    val histories: List<TemplateHistory>,
)

data class TemplateCurrent(
    val id: Long,
    val templateName: String,
    val subject: String,
    val body: String,
    val variables: List<String>,
    val version: Float,
    val createdAt: String,
)

data class TemplateHistory(
    val id: Long,
    val templateId: Long,
    val subject: String,
    val body: String,
    val variables: List<String>,
    val version: Float,
    val createdAt: String,
)