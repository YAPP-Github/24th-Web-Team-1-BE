package com.few.api.domain.article.email.dto

import email.SendMailArgs
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
) {
    companion object {
        fun create(
            memberEmail: String,
            workbookId: Long,
            articleId: Long,
            currentDate: LocalDate,
            category: String,
            articleDay: Int,
            articleTitle: String,
            writerName: String,
            writerLink: URL,
            articleContent: String,
        ) = Content(
            articleLink = URL("https://www.fewletter.com/article/$articleId"),
            currentDate,
            category,
            articleDay,
            articleTitle,
            writerName,
            writerLink,
            articleContent,
            problemLink = URL("https://www.fewletter.com/problem?articleId=$articleId"),
            unsubscribeLink = URL("https://www.fewletter.com/unsubscribe?user=$memberEmail&workbookId=$workbookId&articleId=$articleId")
        )
    }
}