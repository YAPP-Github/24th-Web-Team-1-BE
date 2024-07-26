package com.few.api.security.filter.exception

import com.few.api.security.exception.AccessTokenInvalidException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.time.LocalDateTime

class TokenInvalidExceptionHandlerFilter : OncePerRequestFilter() {
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
        } catch (e: AccessTokenInvalidException) {
            setError(response, e)
        }
    }

    @Throws(IOException::class)
    private fun setError(response: HttpServletResponse, e: Exception) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = CONTENT_TYPE
        val errorResponse = ErrorResponse(LocalDateTime.now())
        response.writer.write(errorResponse.toString())
    }

    private class ErrorResponse(
        private val timestamp: LocalDateTime? = null,
    ) {

        companion object {
            private const val code = "fail.authentication"
            private const val message = "인증이 필요해요."
        }
        override fun toString(): String {
            return (
                "{" +
                    "\"code\" : \"" +
                    code +
                    "\"" +
                    ", \"message\" : \"" +
                    message +
                    "\"" +
                    ", \"timestamp\" : \"" +
                    timestamp +
                    "\"}"
                )
        }
    }
}