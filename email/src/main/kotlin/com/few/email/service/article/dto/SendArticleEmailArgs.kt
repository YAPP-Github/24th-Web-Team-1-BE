package com.few.email.service.article.dto

import com.few.email.sender.dto.SendMailArgs
import java.net.URL
import java.time.LocalDate

class SendArticleEmailArgs(
    to: String,
    subject: String,
    template: String,
    val articleContent: Content,
    val style: String = ""
) : SendMailArgs<Content, String>(to, subject, template, articleContent, style)

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
    val unsubscribeLink: URL
)