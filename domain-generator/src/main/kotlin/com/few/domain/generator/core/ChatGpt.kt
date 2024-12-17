package com.few.domain.generator.core

import com.few.domain.generator.client.OpenAiClient
import com.few.domain.generator.client.request.OpenAiRequest
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
    private fun makeSummaryPrompt(news: NewsModel): List<Map<String, String>> {
        val command = """
            다음 뉴스 기사를 분석하고 요약해주세요:

            제목: ${news.title}
            내용: ${news.content}

            # 지침:
            1. 뉴스 기사를 요약하세요.
            2. 본문에서 가장 중요한 문장 3개를 선택하세요. 이 문장들은 반드시 원문 그대로여야 합니다.
            3. 키워드는 최대 3개, 본문에서 나온 단어로 작성해주세요.
            4. 응답은 반드시 다음 JSON 형식을 따라야 합니다:

            {
                "summary": "여기에 요약을 작성하세요 (최대 100자)",
                "important_sentences": [
                    "여기에 본문에서 추출한 중요한 문장을 그대로 작성하세요",
                    "여기에 본문에서 추출한 두 번째 중요한 문장을 그대로 작성하세요",
                    "여기에 본문에서 추출한 세 번째 중요한 문장을 그대로 작성하세요"
                ],
                "keywords": ["키워드1", "키워드2", "키워드3"]
            }

            # 주의사항:
            - 응답은 오직 위의 JSON 형식만 포함해야 합니다. 다른 설명이나 내용을 추가하지 마세요.
            - 요약은 100자를 넘지 않도록 해주세요.
            - 중요한 문장은 반드시 본문에서 그대로 추출해야 합니다. 수정하거나 재작성하지 마세요.
            - 키워드는 최대 3개, 본문에서 나온 단어로 작성해주세요.
            - 중요한 문장은 정확히 3개를 선택해야 합니다.
        """.trimIndent()

        return listOf(
            mapOf("role" to "system", "content" to "당신은 뉴스 기사를 간결하게 요약하는 전문가입니다. 주어진 뉴스 기사를 분석하고 요약해야 합니다."),
            mapOf("role" to "user", "content" to command)
        )
    }

    fun summarizeNewsWithChatGPT(news: NewsModel): Map<String, Any> {
        val prompt = makeSummaryPrompt(news)
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
        val promptMessages = makeGroupingPrompt(newsList)

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

    private fun makeGroupingPrompt(newsList: List<NewsModel>): List<Map<String, String>> {
        val newsSummaries = newsList.joinToString("\n") { "${it.id}: ${it.summary}" }
        val command = """
            다음은 여러 뉴스 기사의 요약입니다. 이 뉴스들을 비슷한 주제끼리 그룹핑해주세요:
            
            $newsSummaries
            
            # 지침:
            1. 뉴스 요약들을 분석하고 비슷한 주제끼리 그룹화하세요.
            2. 각 그룹에 적절한 주제를 부여하세요.
            3. topic은 구체적인 문장으로 작성해주세요.
            4. 응답은 반드시 다음 JSON 형식을 따라야 합니다:

            {
                "groups": [
                    {
                        "topic": "그룹의 주제",
                        "news_ids": ["id1", "id3", "id5" ...]
                    },
                    {
                        "topic": "다른 그룹의 주제",
                        "news_ids": ["id2", "id4", "id6" ...]
                    }
                ]
            }

            # 주의사항:
            - 응답은 오직 위의 JSON 형식만 포함해야 합니다. 다른 설명이나 내용을 추가하지 마세요.
            - 각 그룹의 "news_ids"는 위 목록의 뉴스 ID를 나타냅니다.
            - 그룹의 수에는 제한이 없지만, 너무 많은 그룹을 만들지 않도록 주의하세요. 뉴스 총 개수의 10% 이상의 그룹을 만들지 않도록 주의하세요.
            - 그룹의 주제는 추상적인 문장을 사용하지 말고 구체적인 문장으로 작성해주세요.
        """.trimIndent()

        return listOf( // TODO 클래스 정의
            mapOf("role" to "system", "content" to "당신은 뉴스 기사를 주제별로 그룹핑하는 전문가입니다. 주어진 뉴스 요약들을 분석하고 비슷한 주제끼리 그룹화해야 합니다."),
            mapOf("role" to "user", "content" to command)
        )
    }
}