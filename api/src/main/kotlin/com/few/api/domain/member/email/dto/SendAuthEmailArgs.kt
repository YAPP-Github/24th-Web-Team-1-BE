package com.few.api.domain.member.email.dto

import email.SendMailArgs
import java.net.URL

data class SendAuthEmailArgs(
    override val to: String,
    override val subject: String,
    override val template: String,
    override val content: Content,
    override val properties: String = "",
) : SendMailArgs<Content, String>

data class Content(
    val headComment: String,
    val subComment: String,
    val email: String,
    val confirmLink: URL,
)