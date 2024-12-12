package web.security.config

import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class WebSecurityConfigurer(
    private val userArgumentHandlerMethodArgumentResolver: HandlerMethodArgumentResolver,
) : WebMvcConfigurer {

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(userArgumentHandlerMethodArgumentResolver)
    }
}