package com.few.email.sender.provider

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import org.springframework.stereotype.Component

@Component
class ArticleAwsSESEmailSendProvider(
    amazonSimpleEmailService: AmazonSimpleEmailService,
    javaEmailSendProvider: JavaEmailSendProvider,
) : AwsSESEmailSendProvider(
    amazonSimpleEmailService,
    javaEmailSendProvider
) {
    override fun getWithConfigurationSetName(): String {
        return "few-article-configuration-set"
    }
}