package com.few.api.domain.batch.controller

import com.few.api.domain.batch.article.ApiBatchSendArticleEmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/batch")
class BatchController(
    private val batchSendArticleEmailService: ApiBatchSendArticleEmailService,
    @Value("\${auth.batch}") private val auth: String,
) {
    @PostMapping("/article")
    fun batchArticle(
        @RequestParam(value = "auth") auth: String,
    ) {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        batchSendArticleEmailService.execute()
    }
}