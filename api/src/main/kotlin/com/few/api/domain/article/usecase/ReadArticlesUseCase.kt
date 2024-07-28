package com.few.api.domain.article.usecase

import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseOut
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.dao.article.record.SelectArticleViewsRecord
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadArticlesUseCase(
    private val articleDao: ArticleDao,
    private val articleViewCountDao: ArticleViewCountDao,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticlesUseCaseIn): ReadArticlesUseCaseOut {
        // 1. 아티클 조회수에서 마지막 읽은 아티클아이디, 카테고리를 기반으로 조회수 상위 10개를 가져옴
        val offset = if (useCaseIn.prevArticleId <= 0) {
            0L
        } else {
            articleViewCountDao.selectRankByViews(
                SelectRankByViewsQuery(useCaseIn.prevArticleId)
            ) ?: 0
        }

        val articleViewsRecords: List<SelectArticleViewsRecord> = articleViewCountDao.selectArticlesOrderByViews(
            SelectArticlesOrderByViewsQuery(
                offset,
                CategoryType.fromCode(useCaseIn.categoryCd)
            )
        )

        // 2. 조회한 10개의 아티클 아이디를 기반으로 로컬 캐시에 있는지 조회
        // 3. 로컬캐시에 없으면 ARTICLE_MAIN_CARD 테이블에서 데이터가 있는지 조회
        // 4. ARTICLE_MAIN_CARD 테이블에도 없으면 조인 진행 후 ARTICLE_MAIN_CARD 테이블 및 캐시에 넣기
        return ReadArticlesUseCaseOut(emptyList()) // TODO: impl
    }
}