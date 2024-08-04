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
class ReadArticlesUseCase(
    private val articleViewCountDao: ArticleViewCountDao,
    private val articleMainCardDao: ArticleMainCardDao,
    private val articleDao: ArticleDao,
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

        // 이번 스크롤에서 보여줄 아티클 ID에 대한 기준 (Criterion)
        val articleViewsRecords: MutableList<SelectArticleViewsRecord> = articleViewCountDao.selectArticlesOrderByViews(
            SelectArticlesOrderByViewsQuery(
                offset,
                CategoryType.fromCode(useCaseIn.categoryCd) ?: CategoryType.All
            )
        ).toMutableList()

        val isLast = if (articleViewsRecords.size == 11) {
            articleViewsRecords.removeAt(10)
            false
        } else {
            true
        }

        // 2. TODO: 조회한 10개의 아티클 아이디를 기반으로 로컬 캐시에 있는지 조회(아티클 단건조회 캐시 사용)
        // 3. TODO: 로컬캐시에 없으면 ARTICLE_MAIN_CARD 테이블에서 데이터가 있는지 조회 (컨텐츠는 article_ifo에서)

        // 4. ARTICLE_MAIN_CARD 테이블에도 없으면 조인 진행 후 ARTICLE_MAIN_CARD 테이블 및 캐시에 넣기 (컨텐츠는 article_ifo에서)
        var existInArticleMainCardRecords: Set<ArticleMainCardRecord> =
            articleMainCardDao.selectArticleMainCardsRecord(articleViewsRecords.map { it.articleId }.toSet())

        // 아티클 컨텐츠 조회
        val selectArticleContentsRecords: List<SelectArticleContentsRecord> =
            articleDao.selectArticleContents(existInArticleMainCardRecords.map { it.articleId }.toSet())
        setContentsToRecords(selectArticleContentsRecords, existInArticleMainCardRecords)

        val sortedArticles = updateAndSortArticleViews(existInArticleMainCardRecords, articleViewsRecords)

        val articleUseCaseOuts: List<ReadArticleUseCaseOut> = sortedArticles.map { a ->
            ReadArticleUseCaseOut(
                id = a.articleId,
                writer = WriterDetail(
                    id = a.writerId,
                    name = a.writerName,
                    url = a.writerImgUrl
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