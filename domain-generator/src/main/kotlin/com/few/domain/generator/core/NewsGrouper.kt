package com.few.domain.generator.core

import com.few.domain.generator.core.model.GroupNews
import com.few.domain.generator.core.model.News
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File

@Component
class NewsGrouper(
    private val chatGpt: ChatGpt,
    private val fewGson: Gson,
) {
    private val log = KotlinLogging.logger {}

    fun groupAndSaveNews(
        inputFilePath: String,
        outputFilePath: String,
    ) {
        val newsList = loadSummarizedNews(inputFilePath)

        log.info { "뉴스 그룹화 진행 중..." }

        val groupedNews = chatGpt.groupNews(newsList)

        log.info { "그룹화된 뉴스 저장 중..." }
        saveGroupedNewsToJson(groupedNews, newsList, outputFilePath)

        log.info { "뉴스 그룹화 완료." }
        log.info { "${groupedNews.size()}개의 그룹으로 뉴스가 분류되어 '$outputFilePath' 파일로 저장되었습니다." }
    }

    private fun loadSummarizedNews(inputFilePath: String): List<News> {
        val fileContent = File(inputFilePath).readText(Charsets.UTF_8)

        // JSON 문자열을 List<Map<String, Any>> 형태로 변환
        val typeToken = object : TypeToken<List<Map<String, Any>>>() {}.type
        val data: List<Map<String, Any>> = fewGson.fromJson(fileContent, typeToken)

        // 각 항목을 NewsModel 객체로 변환
        return data.map { News.fromMap(it) }
    }

    private fun saveGroupedNewsToJson(
        groupedNews: JsonObject,
        newsList: List<News>,
        outputFilePath: String,
    ) {
        val result = mutableListOf<GroupNews>()

        // "groups" 필드를 JsonArray로 추출
        val groupElements = groupedNews.getAsJsonArray("groups")

        for (groupElement in groupElements) {
            val group = groupElement.asJsonObject

            // "news_ids"를 JsonArray로 추출하고 String 리스트로 변환
            val groupNewsIds = group.getAsJsonArray("news_ids").map { it.asString }

            // 뉴스 ID가 그룹에 포함된 뉴스 필터링
            val newsInGroup = newsList.filter { it.id in groupNewsIds }

            // 뉴스가 3개 이상인 경우만 추가
            if (newsInGroup.size >= 3) {
                val groupNews =
                    GroupNews(
                        topic = group.getAsJsonPrimitive("topic").asString,
                        news = newsInGroup,
                    )
                result.add(groupNews)
                log.info { "groupNewsIds: $groupNewsIds" }
            }
        }

        // JSON 직렬화 및 파일 저장
        val groupNewsData = result.map { it.toMap() }
        File(outputFilePath).writeText(fewGson.toJson(groupNewsData), Charsets.UTF_8)
    }
}