package com.few.email.sender.provider

interface EmailSendProvider {
    /**
     * @return 전송한 이메일 식벽을 위한 값
     */
    fun sendEmail(from: String, to: String, subject: String, message: String): String
}