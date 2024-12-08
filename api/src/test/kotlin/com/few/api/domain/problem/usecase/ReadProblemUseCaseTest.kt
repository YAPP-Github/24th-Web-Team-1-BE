package com.few.api.domain.problem.usecase

import com.few.api.domain.problem.usecase.dto.ReadProblemUseCaseIn
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.record.SelectProblemRecord
import com.few.api.domain.problem.repo.support.Content
import com.few.api.domain.problem.repo.support.Contents
import com.few.api.domain.problem.repo.support.ContentsJsonMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
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

    given("특정 문제를 조회하는 요청이 온 상황에서") {
        val problemId = 1L
        val useCaseIn = ReadProblemUseCaseIn(problemId = problemId)

        `when`("문제가 존재할 경우") {
            val title = "title"
            val problemContents = "{}"
            val articleId = 3L
            every { problemDao.selectProblemContents(any()) } returns SelectProblemRecord(id = problemId, title = title, contents = problemContents, articleId = articleId)

            val contentCount = 2
            every { contentsJsonMapper.toObject(any()) } returns Contents(
                IntStream.range(1, 1 + contentCount)
                    .mapToObj { Content(number = it.toLong(), content = "{}") }.toList()
            )

            then("문제 정보를 반환한다") {
                val useCaseOut = useCase.execute(useCaseIn)
                useCaseOut.id shouldBe problemId
                useCaseOut.title shouldBe title
                useCaseOut.contents.size shouldBe contentCount
                useCaseOut.contents.forEachIndexed { index, content ->
                    content.number shouldBe (index + 1).toLong()
                    content.content shouldBe "{}"
                }

                verify(exactly = 1) { problemDao.selectProblemContents(any()) }
                verify(exactly = 1) { contentsJsonMapper.toObject(any()) }
            }
        }

        `when`("문제가 존재하지 않을 경우") {
            every { problemDao.selectProblemContents(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { problemDao.selectProblemContents(any()) }
                verify(exactly = 0) { contentsJsonMapper.toObject(any()) }
            }
        }
    }
})