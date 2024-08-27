package com.few.api.domain.article.event

import com.few.api.domain.article.event.dto.ReadArticleEvent
import com.few.api.domain.article.handler.ArticleViewHisAsyncHandler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ReadArticleEventListener(
    private val articleViewHisAsyncHandler: ArticleViewHisAsyncHandler,
) {

    @EventListener
    fun handleEvent(event: ReadArticleEvent) {
        articleViewHisAsyncHandler.addArticleViewHis(event.articleId, event.memberId, event.category)
    }
}