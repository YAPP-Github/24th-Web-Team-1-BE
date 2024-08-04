package com.few.api.repo.dao.member

import com.few.api.repo.config.LocalCacheConfig.Companion.LOCAL_CM
import com.few.api.repo.config.LocalCacheConfig.Companion.SELECT_WRITER_CACHE
import com.few.api.repo.dao.member.command.InsertMemberCommand
import com.few.api.repo.dao.member.query.BrowseWorkbookWritersQuery
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.query.SelectWritersQuery
import com.few.api.repo.dao.member.record.MemberIdRecord
import com.few.api.repo.dao.member.record.WriterRecord
import com.few.api.repo.dao.member.record.WriterRecordMappedWorkbook
import com.few.data.common.code.MemberType
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.MappingWorkbookArticle
import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.jsonGetAttributeAsText
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class MemberDao(
    private val dslContext: DSLContext,
    private val cacheManager: MemberCacheManager,
) {

    @Cacheable(key = "#query.writerId", cacheManager = LOCAL_CM, cacheNames = [SELECT_WRITER_CACHE])
    fun selectWriter(query: SelectWriterQuery): WriterRecord? {
        return selectWriterQuery(query)
            .fetchOneInto(WriterRecord::class.java)
    }

    fun selectWriterQuery(query: SelectWriterQuery) = dslContext.select(
        Member.MEMBER.ID.`as`(WriterRecord::writerId.name),
        DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(WriterRecord::name.name),
        DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
    )
        .from(Member.MEMBER)
        .where(Member.MEMBER.ID.eq(query.writerId))
        .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
        .and(Member.MEMBER.DELETED_AT.isNull)

    /**
     * 작가 목록 조회 쿼리
     * query의 writerIds에 해당하는 작가 목록을 조회한다.
     * 이때 먼저 cache에 작가 정보가 있는지 확인하고 없는 경우에만 DB에서 조회한다.
     * 조회 이후에는 cache에 저장한다.
     */
    fun selectWriters(query: SelectWritersQuery): List<WriterRecord> {
        val cachedValues = cacheManager.getAllWriterValues().filter { it.writerId in query.writerIds }
        val cachedIdS = cachedValues.map { it.writerId }
        val notCachedIds = query.writerIds.filter { it !in cachedIdS }

        val fetchedValue = selectWritersQuery(notCachedIds)
            .fetchInto(WriterRecord::class.java).let {
                cacheManager.addSelectWorkBookCache(it)
                return@let it
            }

        return cachedValues + fetchedValue
    }

    fun selectWritersQuery(notCachedIds: List<Long>) = dslContext.select(
        Member.MEMBER.ID.`as`(WriterRecord::writerId.name),
        DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name")
            .`as`(WriterRecord::name.name),
        DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(WriterRecord::url.name)
    )
        .from(Member.MEMBER)
        .where(Member.MEMBER.ID.`in`(notCachedIds))
        .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
        .and(Member.MEMBER.DELETED_AT.isNull)
        .orderBy(Member.MEMBER.ID.asc())

    fun selectWriters(query: BrowseWorkbookWritersQuery): List<WriterRecordMappedWorkbook> {
        return selectWritersQuery(query)
            .fetchInto(WriterRecordMappedWorkbook::class.java)
    }

    fun selectWritersQuery(query: BrowseWorkbookWritersQuery) =
        /** workbookId를 기준으로 중복된 writer를 제거하기 위해 distinct를 사용한다. */
        dslContext.selectDistinct(
            DSL.field("article_mapping.${MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.name}")
                .`as`(WriterRecordMappedWorkbook::workbookId.name)
        ).select(
            Member.MEMBER.ID.`as`(WriterRecordMappedWorkbook::writerId.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name")
                .`as`(WriterRecordMappedWorkbook::name.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url")
                .`as`(WriterRecordMappedWorkbook::url.name)
        )
            .from(Member.MEMBER)
            .join(
                /** 조회 workbookId에 포함된 articleId 및 writerId를 조회하기 위해 article_mapping을 사용한다. */
                dslContext.select(
                    ArticleMst.ARTICLE_MST.MEMBER_ID.`as`(ArticleMst.ARTICLE_MST.MEMBER_ID.name),
                    MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.`as`(
                        MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.name
                    )
                )
                    .from(ArticleMst.ARTICLE_MST)
                    .join(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE)
                    .on(ArticleMst.ARTICLE_MST.ID.eq(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.ARTICLE_ID))
                    .where(MappingWorkbookArticle.MAPPING_WORKBOOK_ARTICLE.WORKBOOK_ID.`in`(query.workbookIds))
                    .and(ArticleMst.ARTICLE_MST.DELETED_AT.isNull)
                    .asTable("article_mapping")
            )
            .on(
                Member.MEMBER.ID.eq(
                    DSL.field(
                        "article_mapping.${ArticleMst.ARTICLE_MST.MEMBER_ID.name}",
                        Long::class.java
                    )
                )
            )
            .where(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
            .and(Member.MEMBER.DELETED_AT.isNull)

    fun selectMemberByEmail(query: SelectMemberByEmailQuery): MemberIdRecord? {
        return selectMemberByEmailQuery(query)
            .fetchOneInto(MemberIdRecord::class.java)
    }

    fun selectMemberByEmailQuery(query: SelectMemberByEmailQuery) = dslContext.select(
        Member.MEMBER.ID.`as`(MemberIdRecord::memberId.name),
        jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(MemberIdRecord::writerName.name) // writer only(nullable)
    )
        .from(Member.MEMBER)
        .where(Member.MEMBER.EMAIL.eq(query.email))
        .and(Member.MEMBER.DELETED_AT.isNull)

    fun insertMember(command: InsertMemberCommand): Long? {
        val result = insertMemberCommand(command)
            .returning(Member.MEMBER.ID)
            .fetchOne()

        return result?.getValue(Member.MEMBER.ID)
    }

    fun insertMemberCommand(command: InsertMemberCommand) =
        dslContext.insertInto(Member.MEMBER)
            .set(Member.MEMBER.EMAIL, command.email)
            .set(Member.MEMBER.TYPE_CD, command.memberType.code)
}