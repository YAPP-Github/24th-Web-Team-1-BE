package com.few.domain.generator.core

import com.few.domain.generator.client.OpenAiClient
import com.few.domain.generator.client.request.OpenAiRequest
import com.few.domain.generator.core.model.GroupNews
import com.few.domain.generator.core.model.News
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ChatGpt(
    private val openAiClient: OpenAiClient,
    private val fewGson: Gson,
    private val promptGenerator: PromptGenerator,
    @Value("\${openai.api.model.basic}") private val AI_BASIC_MODEL: String,
    @Value("\${openai.api.model.advanced}") private val AI_ADVANCED_MODEL: String,
) {
    fun summarizeNews(news: News): JsonObject = doAsk(promptGenerator.createSummaryPrompt(news), AI_BASIC_MODEL)

    fun groupNews(newsList: List<News>): JsonObject = doAsk(promptGenerator.createGroupingPrompt(newsList), AI_ADVANCED_MODEL)

    fun summarizeNewsGroup(group: GroupNews): JsonObject = doAsk(promptGenerator.createSummaryPrompt(group), AI_BASIC_MODEL)

    fun refineSummarizedNewsGroup(group: GroupNews): JsonObject = doAsk(promptGenerator.createRefinePrompt(group), AI_BASIC_MODEL)

    /**
     * 공통된 OpenAI 요청 처리 및 JSON 결과 반환
     */
    private fun doAsk(
        prompt: List<Map<String, String>>,
        aiModel: String,
    ): JsonObject {
        val request =
            OpenAiRequest(
                model = aiModel,
                messages = prompt,
            )

        val response = openAiClient.send(request)
        val resultContent =
            response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?.trim()
                ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, JsonObject::class.java)
    }
}