package com.few.api.web.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.logging.MDC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class MDCFilter(private val mapper: ObjectMapper) : WebFilter {
    private val log: Logger = LoggerFactory.getLogger(MDCFilter::class.java)
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestStartTime = System.currentTimeMillis()
        MDC.put("Type", "Request MDC Info")
        MDC.put("TraceId", exchange.request.id)
        MDC.put("Request-Remote-Address", exchange.request.remoteAddress)
        MDC.put("Request-URL", exchange.request.uri)
        MDC.put("Request-Method", exchange.request.method)
        MDC.put(HttpHeaders.REFERER, exchange.request.headers.getFirst(HttpHeaders.REFERER))
        MDC.put(HttpHeaders.USER_AGENT, exchange.request.headers.getFirst(HttpHeaders.USER_AGENT))

        val request = mapOf(
            "Type" to "Request Info",
            "TraceId" to exchange.request.id,
            "Request-Remote-Address" to exchange.request.remoteAddress.toString(),
            "Request-URL" to exchange.request.uri.toString(),
            "Request-Method" to exchange.request.method.toString()
        )
        log.info("{}", mapper.writeValueAsString(request))

        return chain.filter(exchange).doFinally {
            val requestEndTime = System.currentTimeMillis()
            val elapsedTime = requestEndTime - requestStartTime
            MDC.put("ElapsedTime", elapsedTime.toString() + "ms")
            log.info("{}", mapper.writeValueAsString(MDC.getMap()))
            MDC.clear()
        }
    }
}