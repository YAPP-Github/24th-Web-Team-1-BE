package com.few.email.config.porperty

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.mail.provider.aws")
data class AwsEmailProviderProperties(
    var accessKey: String = "",
    var secretKey: String = "",
    var region: String = "",
)