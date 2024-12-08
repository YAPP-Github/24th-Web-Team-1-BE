package com.few.api.domain.admin.usecase.dto

import java.net.URL

data class AddArticleUseCaseIn(
    /** Article MST */
    val writerEmail: String,
    val articleImageUrl: URL,
    val title: String,
    val category: String,
    /** Article IFO */
    val contentType: String,
    val contentSource: String,
    val problems: List<ProblemDetail>,

)

data class ProblemDetail(
    val title: String,
    val contents: List<ProblemContentDetail>,
    val answer: String,
    val explanation: String,
)

data class ProblemContentDetail(
    val number: Long,
    val content: String,
)