package com.few.email.sender.provider

interface EmailSendProvider {
    fun sendEmail(from: String, to: String, subject: String, message: String)
}