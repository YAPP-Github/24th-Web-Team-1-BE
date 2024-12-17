package com.few.domain.generator.core.model

data class GroupNews(
    val topic: String = "",
    val news: List<News> = listOf(),
    var section: SectionContent = SectionContent(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "topic" to topic,
            "news" to news.map { it.toMap() },
            "section" to section.toDict()
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): GroupNews {
            val newsList = (data["news"] as List<Map<String, Any>>).map { News.fromMap(it) }
            val sectionData = SectionContent.fromDict(data["section"] as Map<String, Any>? ?: emptyMap())
            return GroupNews(
                topic = data["topic"] as String,
                news = newsList,
                section = sectionData
            )
        }
    }
}

data class SectionContent(
    val title: String = "",
    val contents: List<Content> = listOf(),
) {
    fun toDict(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "contents" to contents.map { it.toDict() }
        )
    }

    companion object {
        fun fromDict(data: Map<String, Any>): SectionContent {
            val contentsList =
                (data["contents"] as? List<Map<String, Any>>)?.map { Content.fromDict(it) } ?: emptyList()
            return SectionContent(
                title = data["title"] as? String ?: "",
                contents = contentsList
            )
        }
    }
}

data class Content(
    val subTitle: String = "",
    val body: String = "",
) {
    fun toDict(): Map<String, Any> {
        return mapOf(
            "subTitle" to subTitle,
            "body" to body
        )
    }

    companion object {
        fun fromDict(data: Map<String, Any>): Content {
            return Content(
                subTitle = data["subTitle"] as? String ?: "",
                body = data["body"] as? String ?: ""
            )
        }
    }
}