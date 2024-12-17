package com.few.domain.generator.core

import com.few.domain.generator.core.model.NewsModel
import com.google.gson.Gson
import org.springframework.stereotype.Component
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File

@Component
class Extractor(
    private val chatGpt: ChatGpt,
    private val fewGson: Gson,
) {

    private val log = KotlinLogging.logger {}

    fun loadContentFromJson(inputFilePath: String): List<NewsModel> {
        val file = File(inputFilePath)
        if (!file.exists() || !file.canRead()) {
            throw IllegalArgumentException("파일을 찾을 수 없거나 읽을 수 없습니다: $inputFilePath")
        }

        // List<NewsModel> 타입 지정
        val type = object : TypeToken<List<NewsModel>>() {}.type

        return fewGson.fromJson(file.readText(), type)
    }

    suspend fun extractAndSaveNews(inputFilePath: String, outputFilePath: String): Int {
        val newsModels = loadContentFromJson(inputFilePath)
        val semaphore = Semaphore(5) // 최대 동시 실행 개수 제한
        val routines = mutableListOf<Deferred<Unit>>()

        for (newsModel in newsModels) {
            val routine = CoroutineScope(Dispatchers.IO).async {
                semaphore.withPermit {
                    try {
                        val summarizedNews = chatGpt.summarizeNews(newsModel)
                        newsModel.summary = summarizedNews.get("summary")?.asString ?: "요약을 생성할 수 없습니다."
                        newsModel.importantSentences = if (summarizedNews.has("important_sentences")) {
                            val sentencesJsonArray = summarizedNews.getAsJsonArray("important_sentences")
                            sentencesJsonArray.mapNotNull { it.asString }
                        } else {
                            emptyList()
                        }
                    } catch (e: Exception) {
                        println("${newsModel.title}에 대한 요약 중 오류 발생: ${e.message}")
                    }
                }
            }
            routines.add(routine)
        }

        // 진행 상황 출력
        for ((index, routine) in routines.withIndex()) {
            routine.await()
            log.info { "뉴스 요약 진행 중: ${index + 1} / ${newsModels.size}" }
        }

        saveNewsToJson(newsModels, outputFilePath)
        return newsModels.size
    }

    fun saveNewsToJson(newsList: List<NewsModel>, outputFilePath: String) {
        // List<NewsModel>을 JSON 문자열로 변환
        val newsData = newsList.map { it.toMap() }
        val jsonString = fewGson.toJson(newsData)

        // JSON 파일로 저장
        File(outputFilePath).writeText(jsonString, Charsets.UTF_8)
    }
}