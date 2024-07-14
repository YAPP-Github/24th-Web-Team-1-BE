package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.CheckProblemUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.SubmitHistoryDao
import com.few.api.repo.dao.problem.record.SelectProblemAnswerRecord
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CheckProblemUseCaseTest {

    val problemDao: ProblemDao = mockk<ProblemDao>()

    val submitHistoryDao: SubmitHistoryDao = mockk<SubmitHistoryDao>()

    val useCase = CheckProblemUseCase(problemDao, submitHistoryDao)

    @Test
    fun `제출 값과 문제 정답이 같다`() {
        // given
        val submissionVal = "1"
        val answer = submissionVal
        val useCaseIn = CheckProblemUseCaseIn(problemId = 1L, sub = submissionVal)
        val answerRecord = SelectProblemAnswerRecord(id = 1L, answer = answer, explanation = "해설입니다.")

        every { problemDao.selectProblemAnswer(any()) } returns answerRecord
        every { submitHistoryDao.insertSubmitHistory(any()) } returns 1L

        // when
        val useCaseOut = useCase.execute(useCaseIn)

        // then
        assert(useCaseOut.isSolved)
        verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
        verify(exactly = 1) { submitHistoryDao.insertSubmitHistory(any()) }
    }

    @Test
    fun `제출 값과 문제 정답이 다르다`() {
        // given
        val submissionVal = "1"
        val answer = "2"
        val useCaseIn = CheckProblemUseCaseIn(problemId = 1L, sub = submissionVal)
        val answerRecord = SelectProblemAnswerRecord(id = 1L, answer = answer, explanation = "해설입니다.")

        every { problemDao.selectProblemAnswer(any()) } returns answerRecord
        every { submitHistoryDao.insertSubmitHistory(any()) } returns 1L

        // when
        val useCaseOut = useCase.execute(useCaseIn)

        // then
        assert(!useCaseOut.isSolved)
        verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
        verify(exactly = 1) { submitHistoryDao.insertSubmitHistory(any()) }
    }

    @Test
    fun `존재하지 않는 문제일 경우 예외가 발생한다`() {
        // given
        val useCaseIn = CheckProblemUseCaseIn(problemId = 1L, sub = "1")

        every { problemDao.selectProblemAnswer(any()) } returns null

        // when, then
        Assertions.assertThrows(Exception::class.java) { useCase.execute(useCaseIn) }

        verify(exactly = 1) { problemDao.selectProblemAnswer(any()) }
    }
}