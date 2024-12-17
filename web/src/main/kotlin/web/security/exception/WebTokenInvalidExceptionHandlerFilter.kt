package web.security.exception

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import security.exception.SecurityAccessTokenInvalidException
import java.io.IOException

class WebTokenInvalidExceptionHandlerFilter : OncePerRequestFilter() {
    companion object {
        private const val CONTENT_TYPE = "application/json; charset=UTF-8"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: SecurityAccessTokenInvalidException) {
            setError(response, e)
        }
    }

    @Throws(IOException::class)
    private fun setError(
        response: HttpServletResponse,
        e: Exception,
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = CONTENT_TYPE
        val errorResponse = ErrorResponse()
        response.writer.write(errorResponse.toString())
    }

    private class ErrorResponse {
        override fun toString(): String = "{ \"message\": \"Invalid access token\" }"
    }
}