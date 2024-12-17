package com.few.domain.generator.core

import com.google.gson.Gson
import org.springframework.stereotype.Component

@Component
class PromptGenerator(
    private val fewGson: Gson,
) {
    private val jsonTemplate: String = """
        {
            "section": {
                "title": "섹션 제목", // 문장 형태로 작성해야 합니다.
                "contents": [ // 2개 ~ 3개만 작성해야 합니다.
                    {
                        "subTitle": "소제목1", // 문장 형태로 작성해야 합니다.
                        "body": "내용1" // 중요한 문장을 3개 이상 참고해서 작성해야 합니다, 길게 작성해야 합니다.
                    },
                    {
                        "subTitle": "소제목2", // 문장 형태로 작성해야 합니다.
                        "body": "내용2" // 중요한 문장을 3개 이상 참고해서 작성해야 합니다, 길게 작성해야 합니다.
                    }
                ]
            }
        }
    """.trimIndent()

    fun createSummaryPrompt(group: GroupNewsModel): List<Map<String, String>> {
        // 뉴스 요약 문자열 생성
        val newsSummaries = group.news.joinToString("\n") { news ->
            val importantSentences = news.importantSentences.joinToString("\n") { sentence -> "    - $sentence" }
            """
        - ID: ${news.id}
          제목: ${news.title}
          요약: ${news.summary}
          중요한 문장:
        $importantSentences
            """.trimIndent()
        }

        // 프롬프트 명령어 생성
        val command = """
        # 지침
        - 당신은 뉴스 요약 전문 작가입니다.
        - 여러 뉴스를 종합하여 블로그 포스팅 스타일 요약을 작성하는 것이 특기입니다.
        - 항상 정확한 출처 표시와 함께 정보를 제공합니다.
        - 응답은 반드시 JSON 형식이어야 합니다.

        # 제약사항
        - body에서 '중요한 문장'을 참고하여 작성한 경우, 반드시 해당 뉴스의 ID를 사용하여 표시해야 합니다. 
          형식: '[sentence](ID)'
          예시: 
            - 원본 중요한 문장: "삼성전자가 새로운 스마트폰을 출시했다."
            - ID: NEWS001
            - 작성된 문장: [삼성전자가 혁신적인 기능을 탑재한 새로운 스마트폰을 선보였습니다.](NEWS001)
            - 'sentence'을 그대로 사용하지 말아주세요.
        - 모든 중요한 문장은 위와 같은 방식과 형식으로 변형되어 사용되어야 합니다. 그대로 사용하지 말아주세요.

        # 입력문
        다음은 "${group.topic}" 주제에 관한 여러 뉴스 기사의 요약입니다:

        $newsSummaries

        이 뉴스들을 종합하여 섹션별로 구성된 JSON 형식의 요약을 작성해주세요.

        # 출력 JSON 형식
        $jsonTemplate
        """.trimIndent()

        // 프롬프트 반환
        return listOf(
            mapOf("role" to "system", "content" to "당신은 뉴스 요약 전문 작가입니다. 여러 뉴스를 종합하여 블로그 포스팅 스타일의 요약을 작성하는 것이 특기입니다."),
            mapOf("role" to "user", "content" to command)
        )
    }

    fun createRefinePrompt(group: GroupNewsModel): List<Map<String, String>> {
        // GroupNewsModel 객체를 JSON 문자열로 변환
        val groupJson = fewGson.toJson(group.toMap()) // JSON 문자열로 변환

        // 프롬프트 명령어 생성
        val command = """
        # 지침
        - 주어진 블로그 포스트 스타일의 요약을 더 친절하고 매끄럽게 수정해주세요.
        - 말투를 더 부드럽고 친근하게 바꿔주세요.
        - 문장 구조를 자연스럽게 다듬어주세요.
        - 내용의 정확성은 유지하면서 가독성을 높여주세요. 
        - 형식: '[sentence](ID)'은 그대로 유지해야 합니다. '[', ']', '(', ')' 은 제거하지 않습니다. 말투만 수정되어야 합니다.
        - 응답은 반드시 JSON 형식이어야 합니다.

        # 입력문
        $groupJson

        # 출력 JSON 형식
        $jsonTemplate
        """.trimIndent()

        // 프롬프트 반환
        return listOf(
            mapOf("role" to "system", "content" to "당신은 친절하고 매끄러운 글쓰기에 능숙한 편집자입니다."),
            mapOf("role" to "user", "content" to command)
        )
    }
}