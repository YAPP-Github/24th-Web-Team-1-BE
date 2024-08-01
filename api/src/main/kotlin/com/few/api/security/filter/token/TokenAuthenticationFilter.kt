package com.few.api.security.filter.token

import com.few.api.security.exception.AccessTokenInvalidException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import java.util.*

class TokenAuthenticationFilter : AbstractPreAuthenticatedProcessingFilter() {

    private val log = KotlinLogging.logger {}

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any {
        return resolveAccessToken(request)
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
        return resolveAccessToken(request)
    }

    private fun resolveAccessToken(request: HttpServletRequest): String {
        val authorization: String? = request.getHeader("Authorization")
        return authorization?.let {
            AccessTokenResolver.resolve(it)
        } ?: run {
            val exception: AccessTokenInvalidException =
                getAccessTokenInvalidException("Authorization header is missing")
            throw exception
        }
    }

    private fun getAccessTokenInvalidException(message: String): AccessTokenInvalidException {
        val exception = AccessTokenInvalidException(message)
        log.warn { exception.message }
        return exception
    }
}