package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.service.ProblemArticleService
import com.few.api.domain.problem.service.ProblemSubscriptionService
import com.few.api.domain.problem.service.dto.SubscriptionProgressOutDto
import com.few.api.domain.problem.usecase.dto.BrowseUndoneProblemsUseCaseIn
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.SubmitHistoryDao
import com.few.api.domain.problem.repo.record.ProblemIdAndArticleIdRecord
import com.few.api.domain.problem.repo.record.SubmittedProblemIdsRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BrowseUndoneProblemsUseCaseTest : BehaviorSpec({
    lateinit var problemDao: ProblemDao
    lateinit var problemSubscriptionService: ProblemSubscriptionService
    lateinit var problemArticleService: ProblemArticleService
    lateinit var submitHistoryDao: SubmitHistoryDao
    lateinit var useCase: BrowseUndoneProblemsUseCase

    beforeContainer {
        problemDao = mockk<ProblemDao>()
        problemSubscriptionService = mockk<ProblemSubscriptionService>()
        problemArticleService = mockk<ProblemArticleService>()
        submitHistoryDao = mockk<SubmitHistoryDao>()
        useCase = BrowseUndoneProblemsUseCase(problemDao, problemSubscriptionService, problemArticleService, submitHistoryDao)
    }

    given("밀린 문제 ID 조회 요청이 온 상황에서") {
        val memberId = 0L
        val useCaseIn = BrowseUndoneProblemsUseCaseIn(memberId = memberId)

        `when`("밀린 문제가 존재할 경우") {
            every { problemSubscriptionService.browseWorkbookIdAndProgress(any()) } returns listOf(
                SubscriptionProgressOutDto(1L, 3),
                SubscriptionProgressOutDto(2L, 3),
                SubscriptionProgressOutDto(3L, 5),
                SubscriptionProgressOutDto(4L, 7)
            )

            every { problemArticleService.browseArticleIdByWorkbookIdLimitDay(any()) } returns listOf(1L, 2L)

            every { problemDao.selectProblemIdByArticleIds(any()) } returns listOf(
                ProblemIdAndArticleIdRecord(1L, 2L),
                ProblemIdAndArticleIdRecord(2L, 2L)
            )

            every { submitHistoryDao.selectProblemIdByProblemIds(any()) } returns SubmittedProblemIdsRecord(
                listOf(
                    1L,
                    2L
                )
            )

            then("밀린 문제 ID 목록을 반환한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { problemSubscriptionService.browseWorkbookIdAndProgress(any()) }
                verify(exactly = 4) { problemArticleService.browseArticleIdByWorkbookIdLimitDay(any()) }
                verify(exactly = 1) { problemDao.selectProblemIdByArticleIds(any()) }
                verify(exactly = 1) { submitHistoryDao.selectProblemIdByProblemIds(any()) }
            }
        }

        `when`("구독중이 워크북이 없을 경우") {
            every { problemSubscriptionService.browseWorkbookIdAndProgress(any()) } returns emptyList()

            every { problemArticleService.browseArticleIdByWorkbookIdLimitDay(any()) } returns listOf(1L, 2L)

            every { problemDao.selectProblemIdByArticleIds(any()) } returns listOf(
                ProblemIdAndArticleIdRecord(1L, 2L),
                ProblemIdAndArticleIdRecord(2L, 2L)
            )

            every { submitHistoryDao.selectProblemIdByProblemIds(any()) } returns SubmittedProblemIdsRecord(
                listOf(
                    1L,
                    2L
                )
            )

            then("에러를 반환한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { problemSubscriptionService.browseWorkbookIdAndProgress(any()) }
                verify(exactly = 0) { problemArticleService.browseArticleIdByWorkbookIdLimitDay(any()) }
                verify(exactly = 0) { problemDao.selectProblemIdByArticleIds(any()) }
                verify(exactly = 0) { submitHistoryDao.selectProblemIdByProblemIds(any()) }
            }
        }
    }
})