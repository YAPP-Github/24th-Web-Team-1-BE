package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.BrowseProblemsUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.record.ProblemIdsRecord
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BrowseProblemsUseCaseTest {

    val problemDao: ProblemDao = mockk<ProblemDao>()

    val useCase = BrowseProblemsUseCase(problemDao)

    @Test
    fun `특정 아티클에 문제가 존재할 경우 문제번호가 정상적으로 조회된다`() {
        // given
        val useCaseIn = BrowseProblemsUseCaseIn(articleId = 1L)
        val problemIdsRecord = ProblemIdsRecord(listOf(1, 2, 3))

        every { problemDao.selectProblemsByArticleId(any()) } returns problemIdsRecord

        // when
        useCase.execute(useCaseIn)

        // then
        verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
    }

    @Test
    fun `특정 아티클에 문제가 존재하지 않을 경우 예외가 발생한다`() {
        // given
        val useCaseIn = BrowseProblemsUseCaseIn(articleId = 1L)

        every { problemDao.selectProblemsByArticleId(any()) } returns null

        // when / then
        Assertions.assertThrows(Exception::class.java) { useCase.execute(useCaseIn) }
        verify(exactly = 1) { problemDao.selectProblemsByArticleId(any()) }
    }
}