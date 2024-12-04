package com.few.api.domain.batch.article.writer.support

import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.article.email.dto.Content
import com.few.api.domain.article.email.dto.SendArticleEmailArgs
import com.few.api.domain.batch.article.dto.WorkBookSubscriberItem
import com.few.api.domain.batch.article.writer.service.ArticleContent
import com.few.api.domain.batch.article.writer.service.MemberReceiveArticles
import com.few.api.domain.batch.article.writer.service.peek
import java.net.URL
import java.time.LocalDate

data class MailServiceArg(
    val memberId: Long,
    val workbookId: Long,
    val articleId: Long,
    val sendArticleEmailArgs: SendArticleEmailArgs,
)

class MailServiceArgsGenerator(
    private val date: LocalDate,
    private val items: List<WorkBookSubscriberItem>,
    private val memberEmailRecords: Map<Long, String>,
    private val memberReceiveArticles: MemberReceiveArticles,
    private val articleContents: List<ArticleContent>,
) {
    companion object {
        private const val ARTICLE_SUBJECT_TEMPLATE = "Day%d %s"
        private const val ARTICLE_TEMPLATE = "article"
    }

    /**
     * 이메일 전송을 위한 인자 생성
     */
    fun generate(): List<MailServiceArg> {
        return items.map {
            val toEmail = memberEmailRecords[it.memberId]!!
            val memberArticle =
                memberReceiveArticles.getByWorkBookIdAndDayCol(it.targetWorkBookId, it.progress + 1)
            val articleContent = articleContents.peek(memberArticle.articleId).let { article ->
                Content(
                    articleLink = URL("https://www.fewletter.com/article/${memberArticle.articleId}"),
                    currentDate = date,
                    category = CategoryType.convertToDisplayName(article.category.toByte()),
                    articleDay = memberArticle.dayCol.toInt(),
                    articleTitle = article.articleTitle,
                    writerName = article.writerName,
                    writerLink = article.writerLink,
                    articleContent = article.articleContent,
                    problemLink = URL("https://www.fewletter.com/problem?articleId=${memberArticle.articleId}"),
                    unsubscribeLink = URL("https://www.fewletter.com/unsubscribe?user=${memberEmailRecords[it.memberId]}&workbookId=${it.targetWorkBookId}&articleId=${memberArticle.articleId}")
                )
            }

            MailServiceArg(
                it.memberId,
                memberArticle.workbookId,
                memberArticle.articleId,
                SendArticleEmailArgs(
                    toEmail,
                    ARTICLE_SUBJECT_TEMPLATE.format(
                        memberArticle.dayCol,
                        articleContent.articleTitle
                    ),
                    ARTICLE_TEMPLATE,
                    articleContent
                )
            )
        }
    }
}