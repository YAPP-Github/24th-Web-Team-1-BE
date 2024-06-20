package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.BrowseArticleProblemsQuery
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.query.SelectProblemsByArticleIdQuery
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import org.springframework.stereotype.Service

@Service
class BrowseArticleProblemsService(
    private val problemDao: ProblemDao
) {

    fun execute(query: BrowseArticleProblemsQuery): ProblemIdsRecord {
        SelectProblemsByArticleIdQuery(query.articleId).let { query: SelectProblemsByArticleIdQuery ->
            return problemDao.selectProblemsByArticleId(query)
        }
    }
}