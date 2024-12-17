package com.few.domain.generator.core

import com.few.domain.generator.core.model.GroupNewsModel
import com.few.domain.generator.core.model.SectionContentModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File

@Component
class GroupNewsSummarizer(
    private val fewGson: Gson,
    private val chatGpt: ChatGpt,
) {
    private val log = KotlinLogging.logger {}

    fun summarizeAndSaveGroupedNews(inputFilePath: String, outputFilePath: String): Int {
        val groupedNews = loadGroupedNews(inputFilePath)

        for ((index, group) in groupedNews.withIndex()) {
            println("Processing group ${index + 1} / ${groupedNews.size}")
            generateGroupSummary(group)
        }

        saveSummariesToJson(groupedNews, outputFilePath)
        return groupedNews.size
    }

    private fun loadGroupedNews(inputFilePath: String): List<GroupNewsModel> {
        val jsonData = File(inputFilePath).readText(Charsets.UTF_8)

        // JSON 데이터를 List<GroupNewsModel>로 변환
        val listType = object : com.google.gson.reflect.TypeToken<List<GroupNewsModel>>() {}.type
        return fewGson.fromJson(jsonData, listType)
    }

    private fun generateGroupSummary(group: GroupNewsModel): GroupNewsModel {
        // 첫 번째 단계: 요약 생성
        var response = chatGpt.summarizeNewsGroup(group)
        group.section = parseSection(response)

        // 두 번째 단계: refinement
        response = chatGpt.refineSummarizedNewsGroup(group)
        group.section = parseSection(response)

        return group
    }

    private fun parseSection(data: JsonObject): SectionContentModel {
        // "section" 키에 해당하는 JSON 객체를 SectionContentModel로 역직렬화
        val sectionData = data.getAsJsonObject("section")
        return fewGson.fromJson(sectionData, SectionContentModel::class.java)
    }

    private fun saveSummariesToJson(groupedNews: List<GroupNewsModel>, outputFilePath: String) {
        // GroupNewsModel 객체 리스트를 Map 리스트로 변환
        val groupNewsData = groupedNews.map { it.toMap() }

        // JSON 파일로 저장
        File(outputFilePath).writeText(
            fewGson.toJson(groupNewsData),
            Charsets.UTF_8
        )
    }
}