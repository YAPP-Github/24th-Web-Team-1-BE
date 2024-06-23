package com.few.email.sender.dto

abstract class SendMailArgs<C, P>(
    val to: String,
    val subject: String,
    val template: String,
    private val content: C,
    private val properties: P
)