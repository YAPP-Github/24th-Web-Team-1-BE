package com.few.domain.generator.crawler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Component
class NaverNewsCrawler(
    private val maxPages: Int = 100,
    private val maxLinks: Int = 100,
) {
    private val log = KotlinLogging.logger {}
    private val regex_news_links = "https://n\\.news\\.naver\\.com/mnews/article/\\d+/\\d+$"
    private val headers =
        mapOf("User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")

    private fun getSoup(url: String): Document {
        val connection = Jsoup.connect(url)
        headers.forEach { (key, value) ->
            connection.header(key, value)
        }
        return connection.get()
    }

    private fun makeUrl(sid: Int, page: Int) =
        "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=$sid#&date=%2000:00:00&page=$page"

    fun getNaverNewsUrls(sid: Int): List<String> {
        println("$sid 분야의 뉴스 링크를 수집합니다.")
        val allLinks = mutableSetOf<String>()

        for (page in 1..maxPages) {
            val url = makeUrl(sid, page)
            val soup = getSoup(url)

            // Regex to match the desired link pattern
            val pattern = Pattern.compile(regex_news_links)
            val links = soup.select("a[href]").mapNotNull { element ->
                val href = element.attr("href")
                if (pattern.matcher(href).matches()) href else null
            }

            allLinks.addAll(links)

            if (allLinks.size >= maxLinks) {
                break
            }

            Thread.sleep(500) // 0.5 seconds delay
        }

        return allLinks.take(maxLinks).toList()
    }

    fun getNewsContent(url: String): NewsModel? {
        log.info { "뉴스 내용을 가져오는 중: $url" }
        val soup: Document = getSoup(url)

        val title = soup.selectFirst("#title_area > span")
        val date =
            soup.selectFirst("#ct > div.media_end_head.go_trans > div.media_end_head_info.nv_notrans > div.media_end_head_info_datestamp > div:nth-child(1) > span")
        val content = soup.selectFirst("#dic_area")
        val linkElement =
            soup.selectFirst("#ct > div.media_end_head.go_trans > div.media_end_head_info.nv_notrans > div.media_end_head_info_datestamp > a.media_end_head_origin_link")
        val originalLink = linkElement?.attr("href")

        // TODO 원본 데이터 DB 저장으로 변경
        File("soup_content.txt").writeText(soup.outerHtml(), Charsets.UTF_8)

        if (title == null || date == null || content == null) {
            return null
        }

        val dateStr = date.text().trim()
        val dateParts = dateStr.split(" ")

        val dateTime: LocalDateTime = if (dateParts.size == 3) {
            val dateOnly = dateParts[0]
            val amPm = dateParts[1]
            val time = dateParts[2]

            val (hour, minute) = time.split(":").map { it.toInt() }
            val adjustedHour = when {
                amPm == "오후" && hour != 12 -> hour + 12
                amPm == "오전" && hour == 12 -> 0
                else -> hour
            }

            val dateTimeStr = "$dateOnly ${"%02d".format(adjustedHour)}:${"%02d".format(minute)}"
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm"))
        } else {
            LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm"))
        }

        return NewsModel(
            title = title.text().trim(),
            content = content.text().trim(),
            date = dateTime,
            link = url,
            originalLink = originalLink
        )
    }

    fun saveContentAsJson(content: List<NewsModel>) {
        // 콘텐츠를 JSON으로 직렬화
        val jsonContent = Json {
            prettyPrint = true
            encodeDefaults = true
        }.encodeToString(content)

        // TODO DB에 저장
        File("crawled_news.json").writeText(jsonContent, Charsets.UTF_8)
    }
}