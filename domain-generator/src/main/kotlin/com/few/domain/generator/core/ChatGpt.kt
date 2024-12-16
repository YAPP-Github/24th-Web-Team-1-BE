package com.few.domain.generator.core

import com.few.domain.generator.client.OpenAiClient
import com.few.domain.generator.client.request.OpenAiRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Component

@Component
class ChatGpt(
    private val openAiClient: OpenAiClient,
    private val fewGson: Gson,
) {
    fun makeSummaryPrompt(news: NewsModel): List<Map<String, String>> {
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

        val response = openAiClient.summarizeNews(request)
        val resultContent = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("요약 결과를 찾을 수 없습니다.")

        return fewGson.fromJson(resultContent, object : TypeToken<Map<String, Any>>() {}.type)
    }
}