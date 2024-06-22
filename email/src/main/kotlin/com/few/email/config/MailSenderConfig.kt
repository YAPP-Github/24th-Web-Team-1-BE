package com.few.email.config

import com.few.email.config.MailConfig.Companion.BEAN_NAME_PREFIX
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

    @Value("\${spring.mail.protocol}")
    private lateinit var protocol: String

    @Value("\${spring.mail.host}")
    private lateinit var host: String

    @Value("\${spring.mail.port}")
    private lateinit var port: String

    @Value("\${spring.mail.username}")
    private lateinit var username: String

    @Value("\${spring.mail.password}")
    private lateinit var password: String

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private lateinit var auth: String

    @Value("\${spring.mail.properties.mail.smtp.debug}")
    private lateinit var debug: String

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private lateinit var starttls: String

    @Value("\${spring.mail.properties.mail.smtp.EnableSSL.enable}")
    private lateinit var enableSSL: String

    @Bean(name = [MAIL_PROPERTIES])
    fun mailProperties(): MailProperties {
        val mailProperties = MailProperties()
        mailProperties.protocol = protocol
        mailProperties.host = host
        mailProperties.port = port.toInt()
        mailProperties.username = username
        mailProperties.password = password
        mailProperties.properties[MAIL_SMTP_AUTH_KEY] = auth
        mailProperties.properties[MAIL_SMTP_DEBUG_KEY] = debug
        mailProperties.properties[MAIL_SMTP_STARTTLS_ENABLE_KEY] = starttls
        mailProperties.properties[MAIL_SMTP_ENABLE_SSL_ENABLE_KEY] = enableSSL
        return mailProperties
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