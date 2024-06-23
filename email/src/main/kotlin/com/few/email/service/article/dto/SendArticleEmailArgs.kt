package com.few.email.service.article.dto

import com.few.email.sender.dto.SendMailArgs

class SendArticleEmailArgs(
    to: String,
    subject: String,
    template: String,
    val articleContent: String,
    val style: String
) : SendMailArgs<String, String>(to, subject, template, articleContent, style)