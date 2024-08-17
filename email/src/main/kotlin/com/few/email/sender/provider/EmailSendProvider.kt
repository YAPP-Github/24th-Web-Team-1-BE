package com.few.email.sender.provider

interface EmailSendProvider {
    fun sendEmail(form: String, to: String, subject: String, message: String)
}