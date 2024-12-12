package web.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.servlet.HandlerExceptionResolver

class DelegatedAuthenticationEntryPoint(
    private val handlerExceptionResolver: HandlerExceptionResolver,
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        handlerExceptionResolver.resolveException(request, response, null, authException)
    }
}