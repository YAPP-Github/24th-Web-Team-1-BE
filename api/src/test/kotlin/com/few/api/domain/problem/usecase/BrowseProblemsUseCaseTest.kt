package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
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

    given("특정 아티클에 대한") {
        val articleId = 1L
        val useCaseIn = BrowseProblemsUseCaseIn(articleId = articleId)

        `when`("문제가 존재할 경우") {
            every { problemDao.selectProblemsByArticleId(any()) } returns ProblemIdsRecord(listOf(1, 2, 3))

            then("문제번호가 정상적으로 조회된다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
            }
        }

        `when`("문제가 존재하지 않을 경우") {
            every { problemDao.selectProblemsByArticleId(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
            }
        }
    }
})