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
import java.util.stream.IntStream

class ReadProblemUseCaseTest : BehaviorSpec({

    lateinit var problemDao: ProblemDao
    lateinit var contentsJsonMapper: ContentsJsonMapper
    lateinit var useCase: ReadProblemUseCase

    beforeContainer {
        problemDao = mockk<ProblemDao>()
        contentsJsonMapper = mockk<ContentsJsonMapper>()
        useCase = ReadProblemUseCase(problemDao, contentsJsonMapper)
    }

    given("문제를 조회할 상황에서") {
        val problemId = 1L
        val useCaseIn = ReadProblemUseCaseIn(problemId = problemId)

        `when`("문제가 존재할 경우") {
            val title = "title"
            val problemContents = "{}"
            every { problemDao.selectProblemContents(any()) } returns SelectProblemRecord(id = problemId, title = title, contents = problemContents)

            every { contentsJsonMapper.toObject(any()) } returns Contents(
                IntStream.range(1, 3).mapToObj { Content(number = it.toLong(), content = "{}") }.toList()
            )

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