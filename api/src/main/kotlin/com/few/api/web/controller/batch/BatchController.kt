package com.few.api.web.controller.batch

import com.few.batch.service.article.BatchSendArticleEmailService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/batch")
class BatchController(
    private val batchSendArticleEmailService: BatchSendArticleEmailService
) {

    // todo add check permission
    @PostMapping("/article")
    fun batchArticle() {
        batchSendArticleEmailService.execute()
    }
}