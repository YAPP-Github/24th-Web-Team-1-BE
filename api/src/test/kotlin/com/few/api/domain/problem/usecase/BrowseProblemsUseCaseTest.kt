package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.record.ProblemIdsRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BrowseProblemsUseCaseTest : BehaviorSpec({

    lateinit var problemDao: ProblemDao
    lateinit var useCase: BrowseProblemsUseCase

    beforeContainer {
        problemDao = mockk<ProblemDao>()
        useCase = BrowseProblemsUseCase(problemDao)
    }

    given("특정 아티클에 대한 문제 조회 요청이 온 상황에서") {
        val articleId = 1L
        val useCaseIn = BrowseProblemsUseCaseIn(articleId = articleId)

        `when`("아티클의 문제가 존재할 경우") {
            val problemIds = listOf(1L, 2L, 3L)
            every { problemDao.selectProblemsByArticleId(any()) } returns ProblemIdsRecord(problemIds)

            then("문제 목록을 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.problemIds shouldBe problemIds

                verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
            }
        }

        `when`("아티클의 문제가 존재하지 않을 경우") {
            every { problemDao.selectProblemsByArticleId(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
            }
        }
    }
})