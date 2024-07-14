package com.few.email.service.article.dto

import com.few.email.sender.dto.SendMailArgs
import java.net.URL
import java.time.LocalDate

data class SendArticleEmailArgs(
    override val to: String,
    override val subject: String,
    override val template: String,
    override val content: Content,
    override val properties: String = "",
) : SendMailArgs<Content, String>

data class Content(
    val articleLink: URL,
    val currentDate: LocalDate,
    val category: String,
    val articleDay: Int,
    val articleTitle: String,
    val writerName: String,
    val writerLink: URL,
    val articleContent: String,
    val problemLink: URL,
    val unsubscribeLink: URL,
)