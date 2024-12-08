package web.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import web.security.UserArgumentHandlerMethodArgumentResolver

@Configuration
class WebSecurityConfigurer(
    private val userArgumentHandlerMethodArgumentResolver: UserArgumentHandlerMethodArgumentResolver,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(userArgumentHandlerMethodArgumentResolver)
    }
}