package com.few.domain.generator.core

import com.few.domain.generator.client.OpenAiClient
import com.few.domain.generator.client.request.OpenAiRequest
import com.few.domain.generator.core.model.GroupNewsModel
import com.few.domain.generator.core.model.NewsModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Component

@Component
class ChatGpt(
    private val openAiClient: OpenAiClient,
    private val fewGson: Gson,
    private val promptGenerator: PromptGenerator,
) {

    fun summarizeNewsWithChatGPT(news: NewsModel): Map<String, Any> {
        val prompt = promptGenerator.createSummaryPrompt(news)
        val request = OpenAiRequest(
            model = "gpt-4",
            messages = prompt
        )

        val response = openAiClient.send(request)
        val resultContent = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, object : TypeToken<Map<String, Any>>() {}.type)
    }

    fun groupNewsWithChatGPT(newsList: List<NewsModel>): JsonObject {
        val promptMessages = promptGenerator.createGroupingPrompt(newsList)

        val request = OpenAiRequest(
            model = "gpt-4",
            messages = promptMessages
        )

        val response = openAiClient.send(request)
        val resultContent = response.choices[0].message.content.trim()

        // JSON 형태로 변환
        return fewGson.fromJson(resultContent, JsonObject::class.java) // TODO 리턴타입 변경
    }

    fun summarizeNewsGroup(group: GroupNewsModel): JsonObject {
        var prompt = promptGenerator.createSummaryPrompt(group)
        val request = OpenAiRequest(
            model = "gpt-4",
            messages = prompt
        )

        val response = openAiClient.send(request)
        val resultContent = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, JsonObject::class.java)
    }

    fun refineSummarizedNewsGroup(group: GroupNewsModel): JsonObject {
        var prompt = promptGenerator.createRefinePrompt(group)
        val request = OpenAiRequest(
            model = "gpt-4",
            messages = prompt
        )

        val response = openAiClient.send(request)
        val resultContent = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, JsonObject::class.java)
    }
}