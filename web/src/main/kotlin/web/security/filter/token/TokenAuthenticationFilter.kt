package web.security.filter.token

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import security.exception.SecurityAccessTokenInvalidException

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
            val exception: SecurityAccessTokenInvalidException =
                getAccessTokenInvalidException("Authorization header is missing")
            throw exception
        }
    }

    private fun getAccessTokenInvalidException(message: String): SecurityAccessTokenInvalidException {
        val exception = SecurityAccessTokenInvalidException(message)
        log.warn { exception.message }
        return exception
    }
}