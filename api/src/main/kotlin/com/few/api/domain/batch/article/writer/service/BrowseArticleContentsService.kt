package com.few.api.domain.batch.article.writer.service

import com.few.api.domain.common.vo.MemberType
import jooq.jooq_dsl.tables.ArticleIfo
import jooq.jooq_dsl.tables.ArticleMst
import jooq.jooq_dsl.tables.Member
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.net.URL

data class ArticleContent(
    val id: Long,
    val category: String,
    val articleTitle: String,
    val articleContent: String,
    val writerName: String,
    val writerLink: URL,
)

fun List<ArticleContent>.peek(articleId: Long): ArticleContent {
    return this.find {
        it.id == articleId
    } ?: throw IllegalArgumentException("Cannot find article by articleId: $articleId")
}

@Service
class BrowseArticleContentsService(
    private val dslContext: DSLContext,
) {
    /** 구독자들이 받을 아티클 정보를 조회한다 */
    fun execute(articleIds: List<Long>): List<ArticleContent> {
        return dslContext.select(
            ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.`as`(ArticleContent::id.name),
            ArticleIfo.ARTICLE_IFO.CONTENT.`as`(ArticleContent::articleContent.name),
            ArticleMst.ARTICLE_MST.TITLE.`as`(ArticleContent::articleTitle.name),
            ArticleMst.ARTICLE_MST.CATEGORY_CD.`as`(ArticleContent::category.name),
            DSL.jsonGetAttributeAsText(Member.MEMBER.DESCRIPTION, "name").`as`(ArticleContent::writerName.name),
            DSL.jsonGetAttribute(Member.MEMBER.DESCRIPTION, "url").`as`(ArticleContent::writerLink.name)
        )
            .from(ArticleIfo.ARTICLE_IFO)
            .join(ArticleMst.ARTICLE_MST)
            .on(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.eq(ArticleMst.ARTICLE_MST.ID))
            .join(Member.MEMBER)
            .on(
                ArticleMst.ARTICLE_MST.MEMBER_ID.eq(Member.MEMBER.ID)
                    .and(Member.MEMBER.TYPE_CD.eq(MemberType.WRITER.code))
            )
            .where(ArticleIfo.ARTICLE_IFO.ARTICLE_MST_ID.`in`(articleIds))
            .and(ArticleIfo.ARTICLE_IFO.DELETED_AT.isNull)
            .fetchInto(ArticleContent::class.java)
    }
}