package com.few.domain.generator.core.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class News(
    val id: String = UUID.randomUUID().toString().substring(0, 4),
    var title: String = "",
    var date: LocalDateTime = LocalDateTime.now(),
    var content: String = "",
    var link: String = "",
    var summary: String = "",
    var originalLink: String = "",
    var importantSentences: List<String> = emptyList(),
    var keywords: List<String> = emptyList(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "content" to content,
            "date" to date.format(DateTimeFormatter.ISO_DATE_TIME),
            "link" to link,
            "summary" to summary,
            "original_link" to originalLink,
            "important_sentences" to importantSentences,
            "keywords" to keywords
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): News {
            return News(
                id = data["id"] as? String ?: UUID.randomUUID().toString(),
                title = data["title"] as String,
                content = data["content"] as String,
                date = LocalDateTime.parse(data["date"] as String, DateTimeFormatter.ISO_DATE_TIME),
                link = data["link"] as String,
                summary = data["summary"] as? String ?: "",
                originalLink = data["original_link"] as? String ?: "",
                importantSentences = data["important_sentences"] as? List<String> ?: emptyList(),
                keywords = data["keywords"] as? List<String> ?: emptyList()
            )
        }
    }
}