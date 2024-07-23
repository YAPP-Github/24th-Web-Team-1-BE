package com.few.api.repo.explain.subscription

import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery
import com.few.api.repo.explain.ResultGenerator
import com.few.api.repo.jooq.JooqTestSpec
import io.github.oshai.kotlinlogging.KotlinLogging
import jooq.jooq_dsl.tables.*
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@Tag("explain")
class SubscriptionDaoExplainGenerateTest : JooqTestSpec() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var subscriptionDao: SubscriptionDao

    @BeforeEach
    fun setUp() {
        log.debug { "===== start setUp =====" }
        dslContext.deleteFrom(Subscription.SUBSCRIPTION).execute()
        dslContext.deleteFrom(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE).execute()
        dslContext.insertInto(Subscription.SUBSCRIPTION)
            .set(Subscription.SUBSCRIPTION.MEMBER_ID, 1L)
            .set(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID, 1L)
            .set(Subscription.SUBSCRIPTION.PROGRESS, 0)
            .execute()
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, 1L)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, 1L)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, 1)
            .execute()
        log.debug { "===== finish setUp =====" }
    }

    @Test
    fun selectTopWorkbookSubscriptionStatusQuery() {
        val query = SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery(
            memberId = 1L,
            workbookId = 1L
        ).let {
            subscriptionDao.selectTopWorkbookSubscriptionStatusQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "selectTopWorkbookSubscriptionStatusQuery")
    }

    @Test
    fun countWorkbookMappedArticlesQueryExplain() {
        val query = CountWorkbookMappedArticlesQuery(
            workbookId = 1L
        ).let {
            subscriptionDao.countWorkbookMappedArticlesQuery(it)
        }

        val explain = dslContext.explain(query).toString()

        ResultGenerator.execute(query, explain, "countWorkbookMappedArticlesQuery")
    }
}