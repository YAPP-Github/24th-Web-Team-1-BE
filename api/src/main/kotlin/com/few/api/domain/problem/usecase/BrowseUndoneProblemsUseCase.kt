package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.service.ArticleService
import com.few.api.domain.problem.service.SubscriptionService
import com.few.api.domain.problem.service.dto.BrowseArticleIdInDto
import com.few.api.domain.problem.service.dto.BrowseWorkbookIdAndProgressInDto
import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseOut
import com.few.api.domain.problem.usecase.dto.BrowseUndoneProblemsUseCaseIn
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.query.SelectProblemIdByArticleIdsQuery
import com.few.api.repo.dao.problem.query.SelectSubmittedProblemIdsQuery
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrowseUndoneProblemsUseCase(
    private val problemDao: ProblemDao,
    private val subscriptionService: SubscriptionService,
    private val articleService: ArticleService,
    private val submitHistoryDao: SubmitHistoryDao,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: BrowseUndoneProblemsUseCaseIn): BrowseProblemsUseCaseOut {
        /**
         * 유저가 구독한 워크북들에 속한 아티클 개수를 조회함
         * 이때 아티클 개수는 현 시점 기준으로 이메일이 전송된 아티클 개수까지만 조회함
         */
        val subscriptionProgresses = subscriptionService.browseWorkbookIdAndProgress(
            BrowseWorkbookIdAndProgressInDto(useCaseIn.memberId)
        ).takeIf { it.isNotEmpty() } ?: throw NotFoundException("subscribe.workbook.notexist")

        /**
         * 위에서 조회한 워크부에 속한 아티클 개수에 대해 article_id 들을 조회함
         */
        val sentArticleIds = subscriptionProgresses.flatMap { subscriptionProgress ->
            articleService.browseArticleIdByWorkbookIdLimitDay(
                BrowseArticleIdInDto(
                    subscriptionProgress.workbookId,
                    subscriptionProgress.day
                )
            )
        }.toSet()

        /**
         * 위에서 구한 아티클에 속한 모든 problem_id, article_id 조합을 조회함
         */
        val allProblemIdsAndArticleIdsToBeSolved = problemDao.selectProblemIdByArticleIds(
            SelectProblemIdByArticleIdsQuery(sentArticleIds)
        )

        /**
         * 위에서 구한 문제들에 대해 풀이 이력이 존재하는 problem_id만 추출 후
         * 유저가 풀어야 할 전체 problem_id에 대해 여집합 연산
         */
        val allProblemIdsToBeSolved = allProblemIdsAndArticleIdsToBeSolved.map { it.problemId }
        val submittedProblemIds = submitHistoryDao.selectProblemIdByProblemIds(
            SelectSubmittedProblemIdsQuery(useCaseIn.memberId, allProblemIdsToBeSolved)
        ).problemIds

        val unsubmittedProblemIdAndArticleIds: Map<Long, List<Long>> = allProblemIdsAndArticleIdsToBeSolved
            .filter { it.problemId !in submittedProblemIds }
            .groupBy { it.articleId }
            .mapValues { entry -> entry.value.map { it.problemId } }

        /**
         * 결과를 article_id를 기준으로 랜덤화한 뒤 problem_id를 순차적으로 리턴함
         */
        val randomArticleIds = unsubmittedProblemIdAndArticleIds.keys.shuffled()
        val problemIdsRandomizedByArticleId = mutableListOf<Long>()

        randomArticleIds.forEach { articleId ->
            unsubmittedProblemIdAndArticleIds[articleId]?.let { problemIds ->
                problemIdsRandomizedByArticleId.addAll(problemIds)
            }
        }

        return BrowseProblemsUseCaseOut(problemIdsRandomizedByArticleId)
    }
}