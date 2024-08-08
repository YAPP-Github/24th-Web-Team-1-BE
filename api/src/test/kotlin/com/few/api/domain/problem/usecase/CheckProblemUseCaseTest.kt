package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.record.SelectProblemAnswerRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CheckProblemUseCaseTest : BehaviorSpec({

    lateinit var problemDao: ProblemDao
    lateinit var submitHistoryDao: SubmitHistoryDao
    lateinit var useCase: CheckProblemUseCase

    beforeContainer {
        problemDao = mockk<ProblemDao>()
        submitHistoryDao = mockk<SubmitHistoryDao>()
        useCase = CheckProblemUseCase(problemDao, submitHistoryDao)
    }

    given("문제 정답 확인 요청이 온 상황에서") {
        val problemId = 1L
        val submissionVal = "1"
        val useCaseIn = CheckProblemUseCaseIn(problemId = problemId, sub = submissionVal)

        `when`("제출 값과 문제 정답이 같을 경우") {
            val answer = submissionVal
            every { problemDao.selectProblemAnswer(any()) } returns SelectProblemAnswerRecord(id = problemId, answer = answer, explanation = "해설입니다.")

            val problemSubmitHistoryId = 1L
            every { submitHistoryDao.insertSubmitHistory(any()) } returns problemSubmitHistoryId

            then("문제가 정답처리 된다") {
                val useCaseOut = useCase.execute(useCaseIn)

                useCaseOut.isSolved shouldBe true
                verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
                verify(exactly = 1) { submitHistoryDao.insertSubmitHistory(any()) }
            }
        }

        `when`("제출 값과 문제 정답이 다를 경우") {
            val answer = "2"
            every { problemDao.selectProblemAnswer(any()) } returns SelectProblemAnswerRecord(
                id = problemId,
                answer = answer,
                explanation = "해설입니다."
            )

            val problemSubmitHistoryId = 1L
            every { submitHistoryDao.insertSubmitHistory(any()) } returns problemSubmitHistoryId

            then("문제가 오답처리 된다") {
                val useCaseOut = useCase.execute(useCaseIn)

                useCaseOut.isSolved shouldBe false
                verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
                verify(exactly = 1) { submitHistoryDao.insertSubmitHistory(any()) }
            }
        }

        `when`("존재하지 않는 문제일 경우") {
            every { problemDao.selectProblemAnswer(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }
                verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
            }
        }
    }
})