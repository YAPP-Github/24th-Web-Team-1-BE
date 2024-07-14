package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ReadProblemUseCaseTest {

    val problemDao: ProblemDao = mockk<ProblemDao>()

    val contentsJsonMapper: ContentsJsonMapper = mockk<ContentsJsonMapper>()

    val useCase = ReadProblemUseCase(problemDao, contentsJsonMapper)

    @Test
    fun `문제ID로 문제를 조회힌다`() {
        // given
        val useCaseIn = ReadProblemUseCaseIn(problemId = 1L)
        val problemRecord = SelectProblemRecord(id = 1L, title = "title", contents = "{}")
        val contents = Contents(
            listOf(
                Content(number = 1, content = "{}"),
                Content(number = 2, content = "{}")
            )
        )

        every { problemDao.selectProblemContents(any()) } returns problemRecord
        every { contentsJsonMapper.toObject(any()) } returns contents

        // when
        useCase.execute(useCaseIn)

        // then
        verify(exactly = 1) { problemDao.selectProblemContents(any()) }
        verify(exactly = 1) { contentsJsonMapper.toObject(any()) }
    }

    @Test
    fun `문제가 존재하지 않을 경우 예외가 발생한다`() {
        // given
        val useCaseIn = ReadProblemUseCaseIn(problemId = 1L)

        every { problemDao.selectProblemContents(any()) } returns null

        // when, then
        Assertions.assertThrows(Exception::class.java) { useCase.execute(useCaseIn) }

        verify(exactly = 1) { problemDao.selectProblemContents(any()) }
    }
}