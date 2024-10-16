package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseOut
import com.few.api.domain.problem.usecase.dto.BrowseUndoneProblemsUseCaseIn
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectAritlceIdByWorkbookIdAndDayQuery
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.query.SelectProblemIdByArticleIdsQuery
import com.few.api.repo.dao.problem.query.SelectSubmittedProblemIdsQuery
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.query.SelectSubscriptionSendStatusQuery
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrowseUndoneProblemsUseCase(
    private val problemDao: ProblemDao,
    private val subscriptionDao: SubscriptionDao,
    private val articleDao: ArticleDao,
    private val submitHistoryDao: SubmitHistoryDao,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: BrowseUndoneProblemsUseCaseIn): BrowseProblemsUseCaseOut {
        /**
         * 유저가 구독한 워크북들에 속한 아티클 개수를 조회함
         * 이때 아티클 개수는 현 시점 기준으로 이메일이 전송된 아티클 개수까지만 조회함
         */
        val subscriptionProgresses = subscriptionDao.selectWorkbookIdAndProgressByMember(
            SelectSubscriptionSendStatusQuery(useCaseIn.memberId)
        ).takeIf { it.isNotEmpty() } ?: throw NotFoundException("subscribe.workbook.notexist")

        /**
         * 위에서 조회한 워크부에 속한 아티클 개수에 대해 article_id 들을 조회함
         */
        val sentArticleIds = subscriptionProgresses.flatMap { subscriptionProgress ->
            articleDao.selectArticleIdByWorkbookIdLimitDay(
                SelectAritlceIdByWorkbookIdAndDayQuery(
                    subscriptionProgress.workbookId,
                    subscriptionProgress.numOfReadArticle
                )
            ).articleIds
        }.toSet()

        /**
         * 위에서 구한 아티클에 속한 모든 problem_id를 조회함
         */
        val allProblemIdsToBeSolved = problemDao.selectProblemIdByArticleIds(
            SelectProblemIdByArticleIdsQuery(sentArticleIds)
        ).problemIds

        /**
         * 위에서 구한 문제들에 대해 풀이 이력이 존재하는 problem_id만 추출 후
         * 유저가 풀어야 할 전체 problem_id에 대해 여집합 연산
         */
        val submittedProblemIds = submitHistoryDao.selectProblemIdByProblemIds(
            SelectSubmittedProblemIdsQuery(useCaseIn.memberId, allProblemIdsToBeSolved)
        ).problemIds

        val unsubmittedProblemIds = allProblemIdsToBeSolved.filter { it !in submittedProblemIds }

        return BrowseProblemsUseCaseOut(unsubmittedProblemIds)
    }
}