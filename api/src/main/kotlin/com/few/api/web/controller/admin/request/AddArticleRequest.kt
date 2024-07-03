package com.few.api.web.controller.admin.request

import jakarta.validation.constraints.NotBlank
import java.net.URL

data class AddArticleRequest(
    /** Article MST */
    @field:NotBlank(message = "{email.notblank}")
    val writerEmail: String,
    @field:NotBlank(message = "{image.url.notblank}")
    val articleImageUrl: URL,
    @field:NotBlank(message = "{title.notblank}")
    val title: String,
    @field:NotBlank(message = "{category.notblank}")
    val category: String,
    /** Article IFO */
    @NotBlank(message = "{content.type.notblank}")
    val contentType: String,
    @field:NotBlank(message = "{content.source.notblank}")
    val contentSource: String,
    val problemData: List<ProblemDto>
)

data class ProblemDto(
    @field:NotBlank(message = "{problem.title.notblank}")
    val title: String,
    val contents: List<ProblemContentDto>,
    @field:NotBlank(message = "{problem.answer.notblank}")
    val answer: String,
    @field:NotBlank(message = "{problem.explanation.notblank}")
    val explanation: String
)
data class ProblemContentDto(
    @field:NotBlank(message = "{min.problem.number}")
    val number: Long,
    @field:NotBlank(message = "{problem.content.notblank}")
    val content: String
)