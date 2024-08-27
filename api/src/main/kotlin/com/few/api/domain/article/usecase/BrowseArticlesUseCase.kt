package com.few.api.domain.article.usecase

import com.few.api.domain.article.usecase.dto.*
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleMainCardDao
import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.query.SelectArticlesOrderByViewsQuery
import com.few.api.repo.dao.article.query.SelectRankByViewsQuery
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.article.record.SelectArticleContentsRecord
import com.few.api.repo.dao.article.record.SelectArticleViewsRecord
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.Comparator

@Component
class BrowseArticlesUseCase(
    private val articleViewCountDao: ArticleViewCountDao,
    private val articleMainCardDao: ArticleMainCardDao,
    private val articleDao: ArticleDao,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticlesUseCaseIn): ReadArticlesUseCaseOut {
        /**
         * 아티클 조회수 테이블에서 마지막 읽은 아티클 아이디, 카테고리를 기반으로 Offset(테이블 row 순위)을 구함
         */
        val offset = if (useCaseIn.prevArticleId <= 0) {
            0L
        } else {
            articleViewCountDao.selectRankByViews(
                SelectRankByViewsQuery(useCaseIn.prevArticleId)
            ) ?: 0
        }

        /**
         * 구한 Offset을 기준으로 이번 스크롤에서 보여줄 아티클 11개를 뽑아옴
         * 카테고리 별, 조회수 순 11개. 조회수가 같을 경우 최신 아티클이 우선순위를 가짐
         */
        val articleViewsRecords: MutableList<SelectArticleViewsRecord> = articleViewCountDao.selectArticlesOrderByViews(
            SelectArticlesOrderByViewsQuery(
                offset,
                CategoryType.fromCode(useCaseIn.categoryCd) ?: CategoryType.All
            )
        ).toMutableList()

        /**
         * 11개를 조회한 상황에서 11개가 조회되지 않았다면 마지막 스크롤로 판단
         */
        val isLast = if (articleViewsRecords.size == 11) {
            articleViewsRecords.removeAt(10)
            false
        } else {
            true
        }

        /**
         * ARTICLE_MAIN_CARD 테이블에서 이번 스크롤에서 보여줄 10개 아티클 조회 (TODO: 캐싱 적용)
         */
        var articleMainCardRecords: Set<ArticleMainCardRecord> =
            articleMainCardDao.selectArticleMainCardsRecord(articleViewsRecords.map { it.articleId }.toSet())

        /**
         * 아티클 컨텐츠는 ARTICLE_MAIN_CARD가 아닌 ARTICLE_IFO에서 조회 (TODO: 캐싱 적용)
         */
        val selectArticleContentsRecords: List<SelectArticleContentsRecord> =
            articleDao.selectArticleContents(articleMainCardRecords.map { it.articleId }.toSet())
        setContentsToRecords(selectArticleContentsRecords, articleMainCardRecords)

        /**
         * 아티클 조회수 순, 조회수가 같을 경우 최신 아티클이 우선순위를 가지도록 정렬 (TODO: 삭제시 양향도 파악 필요)
         */
        val sortedArticles = updateAndSortArticleViews(articleMainCardRecords, articleViewsRecords)

        val articleUseCaseOuts: List<ReadArticleUseCaseOut> = sortedArticles.map { a ->
            ReadArticleUseCaseOut(
                id = a.articleId,
                writer = WriterDetail(
                    id = a.writerId,
                    name = a.writerName,
                    imageUrl = a.writerImgUrl,
                    url = a.writerUrl
                ),
                mainImageUrl = a.mainImageUrl,
                title = a.articleTitle,
                content = a.content,
                problemIds = emptyList(),
                category = CategoryType.fromCode(a.categoryCd)?.displayName
                    ?: throw NotFoundException("article.invalid.category"),
                createdAt = a.createdAt,
                views = a.views,
                workbooks = a.workbooks
                    .map { WorkbookDetail(it.id!!, it.title!!) }
            )
        }.toList()

        return ReadArticlesUseCaseOut(articleUseCaseOuts, isLast)
    }

    private fun updateAndSortArticleViews(
        articleRecords: Set<ArticleMainCardRecord>,
        articleViewsRecords: List<SelectArticleViewsRecord>,
    ): Set<ArticleMainCardRecord> {
        val sortedSet = TreeSet(
            Comparator<ArticleMainCardRecord> { a1, a2 ->
                // views 값이 null일 경우 0으로 간주
                val views1 = a1.views ?: 0
                val views2 = a2.views ?: 0

                // views 내림차순 정렬
                val viewComparison = views2.compareTo(views1)

                if (viewComparison != 0) {
                    viewComparison
                } else {
                    // views가 같을 경우 articleId 내림차순 정렬(최신글)
                    val articleId1 = a1.articleId
                    val articleId2 = a2.articleId
                    articleId2.compareTo(articleId1)
                }
            }
        )

        val viewsMap = articleViewsRecords.associateBy({ it.articleId }, { it.views })

        articleRecords.forEach { article ->
            val updatedViews = viewsMap[article.articleId] ?: 0
            article.views = updatedViews
            sortedSet.add(article)
        }

        return sortedSet
    }

    private fun setContentsToRecords(
        articleContentsRecords: List<SelectArticleContentsRecord>,
        articleMainCardRecords: Set<ArticleMainCardRecord>,
    ) {
        val articleMainCardRecordsMap: Map<Long, ArticleMainCardRecord> =
            articleMainCardRecords.associateBy { it.articleId }

        articleContentsRecords.map { articleContentRecord ->
            articleMainCardRecordsMap[articleContentRecord.articleId]?.content = articleContentRecord.content
        }
    }
}