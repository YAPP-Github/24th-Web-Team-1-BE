package com.few.domain.generator.core

data class GroupNewsModel(
    val topic: String = "",
    val news: List<NewsModel> = listOf(),
    val section: SectionContentModel = SectionContentModel(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "topic" to topic,
            "news" to news.map { it.toMap() },
            "section" to section.toDict()
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): GroupNewsModel {
            val newsList = (data["news"] as List<Map<String, Any>>).map { NewsModel.fromMap(it) }
            val sectionData = SectionContentModel.fromDict(data["section"] as Map<String, Any>? ?: emptyMap())
            return GroupNewsModel(
                topic = data["topic"] as String,
                news = newsList,
                section = sectionData
            )
        }
    }
}

data class SectionContentModel(
    val title: String = "",
    val contents: List<ContentModel> = listOf(),
) {
    fun toDict(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "contents" to contents.map { it.toDict() }
        )
    }

    companion object {
        fun fromDict(data: Map<String, Any>): SectionContentModel {
            val contentsList =
                (data["contents"] as? List<Map<String, Any>>)?.map { ContentModel.fromDict(it) } ?: emptyList()
            return SectionContentModel(
                title = data["title"] as? String ?: "",
                contents = contentsList
            )
        }
    }
}

data class ContentModel(
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
        fun fromDict(data: Map<String, Any>): ContentModel {
            return ContentModel(
                subTitle = data["subTitle"] as? String ?: "",
                body = data["body"] as? String ?: ""
            )
        }
    }
}