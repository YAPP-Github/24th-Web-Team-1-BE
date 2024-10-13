package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.BrowseUndoneProblemsUseCaseIn
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.record.ArticleIdRecord
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import com.few.api.repo.dao.problem.record.SubmittedProblemIdsRecord
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.record.SubscriptionProgress
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BrowseUndoneProblemsUseCaseTest : BehaviorSpec({
    lateinit var problemDao: ProblemDao
    lateinit var subscriptionDao: SubscriptionDao
    lateinit var articleDao: ArticleDao
    lateinit var submitHistoryDao: SubmitHistoryDao
    lateinit var useCase: BrowseUndoneProblemsUseCase

    beforeContainer {
        problemDao = mockk<ProblemDao>()
        subscriptionDao = mockk<SubscriptionDao>()
        articleDao = mockk<ArticleDao>()
        submitHistoryDao = mockk<SubmitHistoryDao>()
        useCase = BrowseUndoneProblemsUseCase(problemDao, subscriptionDao, articleDao, submitHistoryDao)
    }

    given("밀린 문제 ID 조회 요청이 온 상황에서") {
        val memberId = 0L
        val useCaseIn = BrowseUndoneProblemsUseCaseIn(memberId = memberId)

        `when`("밀린 문제가 존재할 경우") {
            every { subscriptionDao.selectWorkbookIdAndProgressByMember(any()) } returns listOf(
                SubscriptionProgress(1L, 3),
                SubscriptionProgress(2L, 3)
            )

            every { articleDao.selectArticleIdByWorkbookIdLimitDay(any()) } returns ArticleIdRecord(listOf(1L, 2L))

            every { problemDao.selectProblemIdByArticleIds(any()) } returns ProblemIdsRecord(listOf(1L, 2L))

            every { submitHistoryDao.selectProblemIdByProblemIds(any()) } returns SubmittedProblemIdsRecord(
                listOf(
                    1L,
                    2L
                )
            )

            then("밀린 문제 ID 목록을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { subscriptionDao.selectWorkbookIdAndProgressByMember(any()) }
                verify(exactly = 1) { articleDao.selectArticleIdByWorkbookIdLimitDay(any()) }
                verify(exactly = 1) { problemDao.selectProblemIdByArticleIds(any()) }
                verify(exactly = 1) { submitHistoryDao.selectProblemIdByProblemIds(any()) }
            }
        }

        `when`("구독중이 워크북이 없을 경우") {
            every { subscriptionDao.selectWorkbookIdAndProgressByMember(any()) } returns emptyList()

            every { articleDao.selectArticleIdByWorkbookIdLimitDay(any()) } returns ArticleIdRecord(listOf(1L, 2L))

            every { problemDao.selectProblemIdByArticleIds(any()) } returns ProblemIdsRecord(listOf(1L, 2L))

            every { submitHistoryDao.selectProblemIdByProblemIds(any()) } returns SubmittedProblemIdsRecord(
                listOf(
                    1L,
                    2L
                )
            )

            then("에러를 반환한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { subscriptionDao.selectWorkbookIdAndProgressByMember(any()) }
                verify(exactly = 0) { articleDao.selectArticleIdByWorkbookIdLimitDay(any()) }
                verify(exactly = 0) { problemDao.selectProblemIdByArticleIds(any()) }
                verify(exactly = 0) { submitHistoryDao.selectProblemIdByProblemIds(any()) }
            }
        }
    }
})