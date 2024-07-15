package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.record.SelectProblemRecord
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import com.few.api.repo.dao.problem.support.ContentsJsonMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ReadProblemUseCaseTest : BehaviorSpec({

    lateinit var problemDao: ProblemDao
    lateinit var contentsJsonMapper: ContentsJsonMapper
    lateinit var useCase: ReadProblemUseCase
    lateinit var useCaseIn: ReadProblemUseCaseIn

    given("문제를 조회할 상황에서") {
        beforeContainer {
            problemDao = mockk<ProblemDao>()
            contentsJsonMapper = mockk<ContentsJsonMapper>()
            useCase = ReadProblemUseCase(problemDao, contentsJsonMapper)
            useCaseIn = ReadProblemUseCaseIn(problemId = 1L)
        }

        `when`("문제가 존재할 경우") {
            val problemRecord = SelectProblemRecord(id = 1L, title = "title", contents = "{}")
            val contents = Contents(
                listOf(
                    Content(number = 1, content = "{}"),
                    Content(number = 2, content = "{}")
                )
            )

            every { problemDao.selectProblemContents(any()) } returns problemRecord
            every { contentsJsonMapper.toObject(any()) } returns contents

            then("정상적으로 실행되어야 한다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { problemDao.selectProblemContents(any()) }
                verify(exactly = 1) { contentsJsonMapper.toObject(any()) }
            }
        }

        `when`("문제가 존재하지 않을 경우") {
            every { problemDao.selectProblemContents(any()) } returns null

            then("예외가 발생해야 한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { problemDao.selectProblemContents(any()) }
            }
        }
    }
})