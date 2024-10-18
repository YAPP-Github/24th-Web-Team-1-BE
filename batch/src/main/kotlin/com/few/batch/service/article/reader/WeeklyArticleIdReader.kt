package com.few.batch.service.article.reader

import com.few.batch.service.article.dto.ArticleIdAndVewCount
import jooq.jooq_dsl.tables.ArticleViewHis.ARTICLE_VIEW_HIS
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Component
class WeeklyArticleIdReader(
    private val dslContext: DSLContext,
) {

    fun browseWeeklyArticleIds(): List<ArticleIdAndVewCount> {
        val today = LocalDate.now()
        val thisSunday = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
        val lastSunday = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY)).minusWeeks(1)

        return selectArticleIdAndViewCountLimit5(thisSunday, lastSunday)
    }

    fun selectArticleIdAndViewCountLimit5(fromDay: LocalDate, toDay: LocalDate): List<ArticleIdAndVewCount> {
        val viewCount = DSL.count()

        return dslContext.select(
            ARTICLE_VIEW_HIS.ARTICLE_MST_ID.`as`(ArticleIdAndVewCount::articleId.name),
            DSL.count().`as`(ArticleIdAndVewCount::viewCount.name)
        )
            .from(ARTICLE_VIEW_HIS)
            .where(ARTICLE_VIEW_HIS.CREATED_AT.between(fromDay.atStartOfDay(), toDay.atTime(23, 59, 59)))
            .groupBy(ARTICLE_VIEW_HIS.ARTICLE_MST_ID)
            .orderBy(viewCount.desc(), ARTICLE_VIEW_HIS.ARTICLE_MST_ID.desc())
            .limit(5)
            .fetchInto(ArticleIdAndVewCount::class.java)
    }
}