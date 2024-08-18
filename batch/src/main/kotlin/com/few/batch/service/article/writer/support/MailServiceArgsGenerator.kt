package com.few.batch.service.article.writer.support

import com.few.batch.data.common.code.BatchCategoryType
import com.few.batch.service.article.dto.WorkBookSubscriberItem
import com.few.batch.service.article.writer.service.ArticleContent
import com.few.batch.service.article.writer.service.MemberReceiveArticles
import com.few.batch.service.article.writer.service.peek
import com.few.email.service.article.dto.Content
import com.few.email.service.article.dto.SendArticleEmailArgs
import java.net.URL
import java.time.LocalDate

data class MailServiceArg(
    val memberId: Long,
    val workbookId: Long,
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
                    category = BatchCategoryType.convertToDisplayName(article.category.toByte()),
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
                it.targetWorkBookId,
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