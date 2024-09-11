package com.few.api.web.support.method

import com.few.api.security.authentication.authority.AuthorityUtils
import com.few.api.security.filter.token.AccessTokenResolver
import com.few.api.security.token.TokenResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentHandlerMethodArgumentResolver(
    private val tokenResolver: TokenResolver,
) : HandlerMethodArgumentResolver {
    val log = KotlinLogging.logger {}

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(UserArgument::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val authorization: String? = webRequest.getHeader("Authorization")

        val memberId = authorization?.let {
            AccessTokenResolver.resolve(it)
        }.let {
            tokenResolver.resolveId(it)
        } ?: 0L

        val email = authorization?.let {
            AccessTokenResolver.resolve(it)
        }.let {
            tokenResolver.resolveEmail(it)
        } ?: ""

        val authorities = authorization?.let {
            AccessTokenResolver.resolve(it)
        }?.let {
            tokenResolver.resolveRole(it)
        }?.let {
            AuthorityUtils.toAuthorities(it)
        } ?: emptyList()

        return UserArgumentDetails(
            isAuth = authorization != null,
            id = memberId.toString(),
            email = email,
            authorities = authorities
        )
    }
}