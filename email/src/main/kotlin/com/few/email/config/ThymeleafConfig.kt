package com.few.email.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode

@Configuration
class ThymeleafConfig {
    @Bean
    fun htmlTemplateEngine(): TemplateEngine {
        val templateEngine: TemplateEngine = SpringTemplateEngine()
        templateEngine.addTemplateResolver(springResourceTemplateResolver())
        return templateEngine
    }

    @Bean
    fun springResourceTemplateResolver(): SpringResourceTemplateResolver {
        val springResourceTemplateResolver = SpringResourceTemplateResolver()
        springResourceTemplateResolver.order = 1
        springResourceTemplateResolver.prefix = "classpath:templates/"
        springResourceTemplateResolver.suffix = ".html"
        springResourceTemplateResolver.setTemplateMode(TemplateMode.HTML)
        springResourceTemplateResolver.characterEncoding = "UTF-8"
        springResourceTemplateResolver.isCacheable = false
        return springResourceTemplateResolver
    }
}