package com.few.api.domain.batch.article.writer.service

import jooq.jooq_dsl.tables.MappingWorkbookArticle
import org.jooq.DSLContext
import org.springframework.stereotype.Service

data class MemberReceiveArticle(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Long,
)

data class MemberReceiveArticles(
    val articles: List<MemberReceiveArticle>,
) {
    fun getByWorkBookIdAndDayCol(
        workbookId: Long,
        dayCol: Long,
    ): MemberReceiveArticle =
        articles.find {
            it.workbookId == workbookId && it.dayCol == dayCol
        } ?: throw IllegalArgumentException("Cannot find article by workbookId: $workbookId, dayCol: $dayCol")

    fun getArticleIds(): List<Long> =
        articles.map {
            it.articleId
        }
}

@Service
class BrowseMemberReceiveArticlesService(
    private val dslContext: DSLContext,
) {
    /** 회원들이 구독한 학습지와 학습 진행 상태를 기준으로 회원들이 받아야 하는 아티클 정보를 조회한다 */
    fun execute(workbooks: Map<Long, List<Long>>): MemberReceiveArticles =
        workbooks.entries
            .stream()
            .map { (workbookId, progress) ->
                val dayCols = progress.stream().map { it + 1L }.toList()
                dslContext
                    .select(
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.`as`(
                            MemberReceiveArticle::workbookId.name,
                        ),
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID.`as`(MemberReceiveArticle::articleId.name),
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.`as`(MemberReceiveArticle::dayCol.name),
                    ).from(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                    .where(
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.eq(workbookId),
                    ).and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL.`in`(dayCols))
                    .and(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DELETED_AT.isNull)
                    .fetchInto(MemberReceiveArticle::class.java)
            }.flatMap { it.stream() }
            .toList()
            .let {
                MemberReceiveArticles(it)
            }
}