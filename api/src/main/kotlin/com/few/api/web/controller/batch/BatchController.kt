package com.few.api.web.controller.batch

import com.few.batch.service.article.BatchSendArticleEmailService
import com.few.batch.service.article.BatchSendWeeklyArticleEmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/batch")
class BatchController(
    private val batchSendArticleEmailService: BatchSendArticleEmailService,
    private val batchSendWeeklyArticleEmailService: BatchSendWeeklyArticleEmailService,
    @Value("\${auth.batch}") private val auth: String,
) {

    @PostMapping("/article")
    fun batchArticle(@RequestParam(value = "auth") auth: String) {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        batchSendArticleEmailService.execute()
    }

    @GetMapping("/weekly")
    fun batchWeeklyArticle(@RequestParam(value = "auth") auth: String) {
        if (this.auth != auth) {
            throw IllegalAccessException("Invalid Permission")
        }
        batchSendWeeklyArticleEmailService.execute()
    }
}