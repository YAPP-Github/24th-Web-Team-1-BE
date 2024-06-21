package com.few.email.config

import com.few.email.config.MailConfig.Companion.BEAN_NAME_PREFIX
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailSenderConfig {

    companion object {
        const val MAIL_PROPERTIES = BEAN_NAME_PREFIX + "MailProperties"
        const val MAIL_SENDER = BEAN_NAME_PREFIX + "JavaMailSender"

        const val MAIL_SMTP_AUTH_KEY = "mail.smtp.auth"
        const val MAIL_SMTP_DEBUG_KEY = "mail.smtp.debug"
        const val MAIL_SMTP_STARTTLS_ENABLE_KEY = "mail.smtp.starttls.enable"
        const val MAIL_SMTP_ENABLE_SSL_ENABLE_KEY = "mail.smtp.EnableSSL.enable"
    }

    @Primary
    @Bean(name = [MAIL_PROPERTIES])
    fun mailProperties(): MailProperties {
        return MailProperties()
    }

    @Bean(name = [MAIL_SENDER])
    fun javaMailSender(): JavaMailSender {
        val javaMailSender = JavaMailSenderImpl()
        val mailProperties = mailProperties()

        javaMailSender.protocol = mailProperties.protocol
        javaMailSender.host = mailProperties.host
        javaMailSender.port = mailProperties.port
        javaMailSender.username = mailProperties.username
        javaMailSender.password = mailProperties.password

        val props = Properties()
        props[MAIL_SMTP_AUTH_KEY] = mailProperties.properties[MAIL_SMTP_AUTH_KEY]
        props[MAIL_SMTP_DEBUG_KEY] = mailProperties.properties[MAIL_SMTP_DEBUG_KEY]
        props[MAIL_SMTP_STARTTLS_ENABLE_KEY] =
            mailProperties.properties[MAIL_SMTP_STARTTLS_ENABLE_KEY]
        props[MAIL_SMTP_ENABLE_SSL_ENABLE_KEY] = mailProperties.properties[MAIL_SMTP_ENABLE_SSL_ENABLE_KEY]

        javaMailSender.setJavaMailProperties(props)
        return javaMailSender
    }
}