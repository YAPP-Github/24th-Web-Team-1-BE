package com.few.domain.generator.client

import com.few.domain.generator.client.request.OpenAiRequest
import com.few.domain.generator.client.response.OpenAiResponse
import com.few.domain.generator.config.OpenAiFeignConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "openAiClient",
    url = "\${openai.api.url}",
    configuration = [OpenAiFeignConfiguration::class]
)
interface OpenAiClient {
    @PostMapping
    fun send(@RequestBody request: OpenAiRequest): OpenAiResponse
}