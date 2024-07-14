package com.few.api.web.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class MDCFilter(private val mapper: ObjectMapper) : Filter {
    private val log: Logger = LoggerFactory.getLogger(MDCFilter::class.java)

    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?,
    ) {
        val requestStartTime = System.currentTimeMillis()
        val traceId = RandomStringUtils.randomAlphanumeric(10)
        MDC.put("Type", "Request MDC Info")
        MDC.put("RequestId", request!!.requestId)
        MDC.put("Request-Remote-Address", request.remoteAddr)
        MDC.put("Request-URL", (request as HttpServletRequest).requestURL.toString())
        MDC.put("Request-Method", request.method)
        MDC.put("TraceId", traceId)
        MDC.put(HttpHeaders.REFERER, request.getHeader(HttpHeaders.REFERER))
        MDC.put(HttpHeaders.USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT))

        chain!!.doFilter(request, response)

        val requestEndTime = System.currentTimeMillis()
        val elapsedTime = requestEndTime - requestStartTime
        MDC.put("ElapsedTime", elapsedTime.toString() + "ms")
        log.info("{}", mapper.writeValueAsString(MDC.getCopyOfContextMap()))
        MDC.clear()
    }
}