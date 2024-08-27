package com.few.api.repo.explain.subscription

import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.*
import com.few.api.repo.dao.subscription.query.CountWorkbookMappedArticlesQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookActiveSubscription
import com.few.api.repo.dao.subscription.query.SelectAllWorkbookSubscriptionStatusNotConsiderDeletedAtQuery
import com.few.api.repo.dao.subscription.query.SelectAllMemberWorkbookInActiveSubscription
import com.few.api.repo.explain.ExplainGenerator
import com.few.api.repo.explain.InsertUpdateExplainGenerator
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

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectTopWorkbookSubscriptionStatusQueryExplain")
    }

    @Test
    fun selectAllWorkbookInActiveSubscriptionStatusQueryExplain() {
        val query = SelectAllMemberWorkbookInActiveSubscription(
            memberId = 1L,
            unsubOpinion = "receive.all"
        ).let {
            subscriptionDao.selectAllWorkbookInActiveSubscriptionStatusQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectAllWorkbookInActiveSubscriptionStatusQueryExplain")
    }

    @Test
    fun selectAllWorkbookActiveSubscriptionStatusQueryExplain() {
        val query = SelectAllMemberWorkbookActiveSubscription(
            memberId = 1L
        ).let {
            subscriptionDao.selectAllWorkbookActiveSubscriptionStatusQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "selectAllWorkbookActiveSubscriptionStatusQueryExplain")
    }

    @Test
    fun countWorkbookMappedArticlesQueryExplain() {
        val query = CountWorkbookMappedArticlesQuery(
            workbookId = 1L
        ).let {
            subscriptionDao.countWorkbookMappedArticlesQuery(it)
        }

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "countWorkbookMappedArticlesQueryExplain")
    }

    @Test
    fun insertWorkbookSubscriptionCommandExplain() {
        val command = InsertWorkbookSubscriptionCommand(
            memberId = 1L,
            workbookId = 1L
        ).let {
            subscriptionDao.insertWorkbookSubscriptionCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "insertWorkbookSubscriptionCommandExplain")
    }

    @Test
    fun updateDeletedAtInAllSubscriptionCommandExplain() {
        val command = UpdateDeletedAtInAllSubscriptionCommand(
            memberId = 1L,
            opinion = "test"
        ).let {
            subscriptionDao.updateDeletedAtInAllSubscriptionCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateDeletedAtInAllSubscriptionCommandExplain")
    }

    @Test
    fun countAllWorkbookSubscriptionQueryExplain() {
        val query = subscriptionDao.countAllWorkbookSubscriptionQuery()

        val explain = ExplainGenerator.execute(dslContext, query)

        ResultGenerator.execute(query, explain, "countAllWorkbookSubscriptionQueryExplain")
    }

    @Test
    fun reSubscribeWorkBookSubscriptionCommandExplain() {
        val command = InsertWorkbookSubscriptionCommand(
            memberId = 1L,
            workbookId = 1L
        ).let {
            subscriptionDao.reSubscribeWorkBookSubscriptionCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "reSubscribeWorkBookSubscriptionCommandExplain")
    }

    @Test
    fun updateDeletedAtInWorkbookSubscriptionCommandExplain() {
        val command = UpdateDeletedAtInWorkbookSubscriptionCommand(
            memberId = 1L,
            workbookId = 1L,
            opinion = "test"
        ).let {
            subscriptionDao.updateDeletedAtInWorkbookSubscriptionCommand(it)
        }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateDeletedAtInWorkbookSubscriptionCommandExplain")
    }

    @Test
    fun updateArticleProgressCommandExplain() {
        val command = UpdateArticleProgressCommand(1L, 1L)
            .let {
                subscriptionDao.updateArticleProgressCommand(it)
            }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateArticleProgressCommandExplain")
    }

    @Test
    fun updateLastArticleProgressCommandExplain() {
        val command = UpdateLastArticleProgressCommand(1L, 1L)
            .let {
                subscriptionDao.updateLastArticleProgressCommand(it)
            }

        val explain = InsertUpdateExplainGenerator.execute(dslContext, command.sql, command.bindValues)

        ResultGenerator.execute(command, explain, "updateLastArticleProgressCommandExplain")
    }
}