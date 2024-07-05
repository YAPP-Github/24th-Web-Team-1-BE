package com.few.email.sender.dto

interface SendMailArgs<C, P> {
    val to: String
    val subject: String
    val template: String
    val content: C
    val properties: P
}