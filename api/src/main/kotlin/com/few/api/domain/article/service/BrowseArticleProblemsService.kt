package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsQuery
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import org.springframework.stereotype.Service

@Service
class BrowseArticleProblemsService(
    private val problemDao: ProblemDao
) {

    fun execute(query: BrowseArticleProblemIdsQuery): ProblemIdsRecord {
        SelectProblemsByArticleIdQuery(query.articleId).let { query: SelectProblemsByArticleIdQuery ->
            return problemDao.selectProblemsByArticleId(query) ?: throw IllegalArgumentException("cannot find problems by articleId: ${query.articleId}") // todo 에러 표준화
        }
    }
}