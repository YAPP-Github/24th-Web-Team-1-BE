package com.few.email.service.member.dto

import com.few.email.sender.dto.SendMailArgs
import java.net.URL

data class SendAuthEmailArgs(
    override val to: String,
    override val subject: String,
    override val template: String,
    override val content: Content,
    override val properties: String = "",
) : SendMailArgs<Content, String>

data class Content(
    val email: String,
    val confirmLink: URL,
)