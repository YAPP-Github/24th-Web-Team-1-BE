package web.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.security.access.AccessDeniedException

class DelegatedAccessDeniedHandler(
    private val handlerExceptionResolver: HandlerExceptionResolver,
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        handlerExceptionResolver.resolveException(
            request,
            response,
            null,
            accessDeniedException
        )
    }
}