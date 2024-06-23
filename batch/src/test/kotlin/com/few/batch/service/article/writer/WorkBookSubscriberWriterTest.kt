package com.few.batch.service.article.writer

import com.few.batch.BatchReaderWriterTestSpec
import com.few.email.service.problem.SendArticleEmailService
import com.few.email.service.problem.dto.SendArticleEmailArgs
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Member
import jooq.jooq_dsl.tables.Subscription
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import kotlin.random.Random

class WorkBookSubscriberWriterTest : BatchReaderWriterTestSpec() {

    @Autowired
    private lateinit var helper: WorkBookSubscriberWriterTestSetHelper

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var workBookSubscriberWriter: WorkBookSubscriberWriter

    @MockBean
    private lateinit var sendArticleEmailService: SendArticleEmailService

    @BeforeEach
    fun setUp() {
        dslContext.deleteFrom(Member.MEMBER).execute()
        dslContext.deleteFrom(Subscription.SUBSCRIPTION).execute()
        dslContext.deleteFrom(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE).execute()
        dslContext.deleteFrom(ArticleIfo.ARTICLE_IFO).execute()

        // setup member
        helper.setUpMembers(10)

        // setup subscription
        helper.setUpSubscriptions(start = 1, count = 5, workbookId = 1)
        helper.setUpSubscriptions(start = 6, count = 10, workbookId = 2)

        // setup mapping workbook article
        helper.setUpMappingWorkbookArticle(start = 1, count = 5, workbookId = 1)
        helper.setUpMappingWorkbookArticle(start = 6, count = 10, workbookId = 2, dayCorrection = 5)

        // setup article ifo
        helper.setUpArticleIfo(start = 1, count = 10)
    }

    @Test
    fun `WorkBookSubscriberWriter가 모두 정상적으로 동작하는 경우`() {
        // given
        val items = helper.browseItems(listOf(1, 2))

        // when
        workBookSubscriberWriter.execute(items.toServiceDto())

        // then
        val results = dslContext.select(Subscription.SUBSCRIPTION.MEMBER_ID, Subscription.SUBSCRIPTION.PROGRESS)
            .from(Subscription.SUBSCRIPTION)
            .where(Subscription.SUBSCRIPTION.MEMBER_ID.`in`(items.toMemberIds()))
            .fetch()
            .intoMap(Subscription.SUBSCRIPTION.MEMBER_ID, Subscription.SUBSCRIPTION.PROGRESS)

        assertEquals(10, results.size)
        items.forEach {
            assertEquals(it.progress + 1, results[it.memberId])
        }
    }

    @Test
    fun `WorkBookSubscriberWriter가 일부 이메일 전송 과정에서 비정상적으로 동작하는 경우`() {
        // given
        val items = helper.browseItems(listOf(1, 2))
        val failItem = items[Random.nextInt(6, items.size - 1)]
        `when`(
            sendArticleEmailService.send(
                SendArticleEmailArgs(
                    "member${failItem.memberId}@gmail.com",
                    "${LocalDate.now()} 일자 학습 아티클",
                    "article",
                    failItem.content,
                    "style"
                )
            )
        ).thenThrow(RuntimeException("send email error"))

        // when
        workBookSubscriberWriter.execute(items.toServiceDto())

        // then
        val results = dslContext.select(Subscription.SUBSCRIPTION.MEMBER_ID, Subscription.SUBSCRIPTION.PROGRESS)
            .from(Subscription.SUBSCRIPTION)
            .where(Subscription.SUBSCRIPTION.MEMBER_ID.`in`(items.toMemberIds()))
            .fetch()
            .intoMap(Subscription.SUBSCRIPTION.MEMBER_ID, Subscription.SUBSCRIPTION.PROGRESS)

        assertEquals(10, results.size)
        items.forEach {
            if (failItem.memberId == it.memberId) {
                assertEquals(it.progress, results[it.memberId])
            } else {
                assertEquals(it.progress + 1, results[it.memberId])
            }
        }
    }
}