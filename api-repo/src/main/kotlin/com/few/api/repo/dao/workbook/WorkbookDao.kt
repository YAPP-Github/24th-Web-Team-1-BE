package com.few.api.repo.dao.workbook

import com.few.api.repo.config.LocalCacheConfig
import com.few.api.repo.config.LocalCacheConfig.Companion.LOCAL_CM
import com.few.api.repo.dao.workbook.command.InsertWorkBookCommand
import com.few.api.repo.dao.workbook.command.MapWorkBookToArticleCommand
import com.few.api.repo.dao.workbook.query.BrowseWorkBookQueryWithSubscriptionCount
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecord
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecordWithSubscriptionCount
import com.few.data.common.code.CategoryType
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Subscription
import jooq.jooq_dsl.tables.Workbook
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.cache.annotation.Cacheable
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class WorkbookDao(
    private val dslContext: DSLContext,
) {
    @Cacheable(key = "#query.id", cacheManager = LOCAL_CM, cacheNames = [LocalCacheConfig.SELECT_WORKBOOK_RECORD_CACHE])
    fun selectWorkBook(query: SelectWorkBookRecordQuery): SelectWorkBookRecord? {
        return selectWorkBookQuery(query)
            .fetchOneInto(SelectWorkBookRecord::class.java)
    }

    fun selectWorkBookQuery(query: SelectWorkBookRecordQuery) =
        dslContext.select(
            Workbook.WORKBOOK.ID.`as`(SelectWorkBookRecord::id.name),
            Workbook.WORKBOOK.TITLE.`as`(SelectWorkBookRecord::title.name),
            Workbook.WORKBOOK.MAIN_IMAGE_URL.`as`(SelectWorkBookRecord::mainImageUrl.name),
            Workbook.WORKBOOK.CATEGORY_CD.`as`(SelectWorkBookRecord::category.name),
            Workbook.WORKBOOK.DESCRIPTION.`as`(SelectWorkBookRecord::description.name),
            Workbook.WORKBOOK.CREATED_AT.`as`(SelectWorkBookRecord::createdAt.name)
        )
            .from(Workbook.WORKBOOK)
            .where(Workbook.WORKBOOK.ID.eq(query.id))
            .and(Workbook.WORKBOOK.DELETED_AT.isNull)

    fun insertWorkBook(command: InsertWorkBookCommand): Long? {
        return insertWorkBookCommand(command)
            .returning(Workbook.WORKBOOK.ID)
            .fetchOne()
            ?.id
    }

    fun insertWorkBookCommand(command: InsertWorkBookCommand) =
        dslContext.insertInto(Workbook.WORKBOOK)
            .set(Workbook.WORKBOOK.TITLE, command.title)
            .set(Workbook.WORKBOOK.MAIN_IMAGE_URL, command.mainImageUrl.toString())
            .set(Workbook.WORKBOOK.CATEGORY_CD, CategoryType.convertToCode(command.category))
            .set(Workbook.WORKBOOK.DESCRIPTION, command.description)

    fun mapWorkBookToArticle(command: MapWorkBookToArticleCommand) {
        dslContext.insertInto(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID, command.workbookId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID, command.articleId)
            .set(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.DAY_COL, command.dayCol)
            .execute()
    }

    /**
     * category에 따라서 조회된 구독자 수가 포함된 Workbook 목록을 반환한다.
     * 정렬 순서는 구독자 수가 많은 순서로, 구독자 수가 같다면 생성일자가 최신인 순서로 반환한다.
     */
    fun browseWorkBookWithSubscriptionCount(query: BrowseWorkBookQueryWithSubscriptionCount): List<SelectWorkBookRecordWithSubscriptionCount> {
        return browseWorkBookQuery(query)
            .fetchInto(SelectWorkBookRecordWithSubscriptionCount::class.java)
    }

    fun browseWorkBookQuery(query: BrowseWorkBookQueryWithSubscriptionCount) =
        dslContext.select(
            Workbook.WORKBOOK.ID.`as`(SelectWorkBookRecordWithSubscriptionCount::id.name),
            Workbook.WORKBOOK.TITLE.`as`(SelectWorkBookRecordWithSubscriptionCount::title.name),
            Workbook.WORKBOOK.MAIN_IMAGE_URL.`as`(SelectWorkBookRecordWithSubscriptionCount::mainImageUrl.name),
            Workbook.WORKBOOK.CATEGORY_CD.`as`(SelectWorkBookRecordWithSubscriptionCount::category.name),
            Workbook.WORKBOOK.DESCRIPTION.`as`(SelectWorkBookRecordWithSubscriptionCount::description.name),
            Workbook.WORKBOOK.CREATED_AT.`as`(SelectWorkBookRecordWithSubscriptionCount::createdAt.name),
            /** 구독자 수가 없다면 0으로 반환한다. */
            DSL.coalesce(
                DSL.field("subscription_count_table.subscription_count", Long::class.java),
                0
            ).`as`(SelectWorkBookRecordWithSubscriptionCount::subscriptionCount.name)
        )
            .from(Workbook.WORKBOOK)
            /** 구독자가 없는 Workbook도 조회하기 위해 LEFT JOIN을 사용한다. */
            .leftJoin(
                /** Subscription 테이블을 이용하여 구독자 수를 조회한다. */
                dslContext.select(
                    Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.`as`(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.name),
                    DSL.count(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID)
                        .`as`("subscription_count")
                )
                    .from(Subscription.SUBSCRIPTION)
                    .where(Subscription.SUBSCRIPTION.DELETED_AT.isNull)
                    .groupBy(Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID)
                    .asTable("subscription_count_table")
            )
            .on(
                Workbook.WORKBOOK.ID.eq(
                    DSL.field(
                        "subscription_count_table.${Subscription.SUBSCRIPTION.TARGET_WORKBOOK_ID.name}",
                        Long::class.java
                    )
                )
            )
            .where(browseWorkBookCategoryCondition(query))
            .and(Workbook.WORKBOOK.DELETED_AT.isNull)
            /** 구독자 수가 많은 순서로, 구독자 수가 같다면 생성일자가 최신인 순서로 정렬한다. */
            .orderBy(
                DSL.field("subscription_count_table.subscription_count", Long::class.java).desc(),
                Workbook.WORKBOOK.CREATED_AT.desc()
            )
            .query

    /**
     * category에 따라서 조건을 생성한다.
     */
    private fun browseWorkBookCategoryCondition(query: BrowseWorkBookQueryWithSubscriptionCount): Condition {
        return when (query.category) {
            (-1).toByte() -> DSL.noCondition()
            else -> Workbook.WORKBOOK.CATEGORY_CD.eq(query.category)
        }
    }
}