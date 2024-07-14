package com.few.api.repo.dao.article.command

import java.net.URL

data class InsertFullArticleRecordCommand(
    val writerId: Long,
    val mainImageURL: URL,
    val title: String,
    val category: Byte,
    val content: String,
)