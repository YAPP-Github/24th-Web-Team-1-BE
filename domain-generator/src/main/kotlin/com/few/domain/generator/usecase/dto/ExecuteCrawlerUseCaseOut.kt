package com.few.domain.generator.usecase.dto

data class ExecuteCrawlerUseCaseOut(
    val sid: Int,
    val crawlingIds: List<String>,
)