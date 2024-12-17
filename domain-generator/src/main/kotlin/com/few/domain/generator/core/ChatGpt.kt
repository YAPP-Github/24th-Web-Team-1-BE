package com.few.domain.generator.core

import com.few.domain.generator.client.OpenAiClient
import com.few.domain.generator.client.request.OpenAiRequest
import com.few.domain.generator.core.model.GroupNewsModel
import com.few.domain.generator.core.model.NewsModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.stereotype.Component

@Component
class ChatGpt(
    private val openAiClient: OpenAiClient,
    private val fewGson: Gson,
    private val promptGenerator: PromptGenerator,
) {

    fun summarizeNews(news: NewsModel): JsonObject =
        doAsk(promptGenerator.createSummaryPrompt(news))

    fun groupNews(newsList: List<NewsModel>): JsonObject =
        doAsk(promptGenerator.createGroupingPrompt(newsList))

    fun summarizeNewsGroup(group: GroupNewsModel): JsonObject =
        doAsk(promptGenerator.createSummaryPrompt(group))

    fun refineSummarizedNewsGroup(group: GroupNewsModel): JsonObject =
        doAsk(promptGenerator.createRefinePrompt(group))

    /**
     * 공통된 OpenAI 요청 처리 및 JSON 결과 반환
     */
    private fun doAsk(prompt: List<Map<String, String>>): JsonObject {
        val request = OpenAiRequest(
            model = "gpt-4",
            messages = prompt
        )

        val response = openAiClient.send(request)
        val resultContent = response.choices.firstOrNull()?.message?.content?.trim()
            ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, JsonObject::class.java)
    }
}