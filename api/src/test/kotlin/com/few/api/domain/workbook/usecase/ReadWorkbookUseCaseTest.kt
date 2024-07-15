package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookArticleService
import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.dto.WorkBookArticleOutDto
import com.few.api.domain.workbook.service.dto.WriterOutDto
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecord
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

import java.net.URL
import java.time.LocalDateTime

class ReadWorkbookUseCaseTest : BehaviorSpec({
    lateinit var workbookDao: WorkbookDao
    lateinit var workbookArticleService: WorkbookArticleService
    lateinit var workbookMemberService: WorkbookMemberService
    lateinit var useCase: ReadWorkbookUseCase
    val useCaseIn = ReadWorkbookUseCaseIn(workbookId = 1L)

    beforeContainer {
        workbookDao = mockk<WorkbookDao>()
        workbookArticleService = mockk<WorkbookArticleService>()
        workbookMemberService = mockk<WorkbookMemberService>()
        useCase = ReadWorkbookUseCase(workbookDao, workbookArticleService, workbookMemberService)
    }

    given("워크북 조회 요청이 온 상황에서") {
        `when`("워크북과 작가가 존재할 경우") {
            every { workbookDao.selectWorkBook(any()) } returns SelectWorkBookRecord(
                id = 1L,
                title = "workbook title",
                mainImageUrl = URL("https://jh-labs.tistory.com/"),
                category = (10).toByte(),
                description = "workbook description",
                createdAt = LocalDateTime.now()
            )
            every { workbookArticleService.browseWorkbookArticles(any()) } returns listOf(
                WorkBookArticleOutDto(
                    articleId = 1L,
                    writerId = 1L,
                    mainImageURL = URL("https://jh-labs.tistory.com/"),
                    title = "article title",
                    category = (10).toByte(),
                    content = "article description",
                    createdAt = LocalDateTime.now()
                )
            )
            every { workbookMemberService.browseWriterRecords(any()) } returns listOf(
                WriterOutDto(
                    writerId = 1L,
                    name = "hunca",
                    url = URL("https://jh-labs.tistory.com/")
                )
            )

            then("워크북 정상 조회된다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { workbookDao.selectWorkBook(any()) }
                verify(exactly = 1) { workbookArticleService.browseWorkbookArticles(any()) }
                verify(exactly = 1) { workbookMemberService.browseWriterRecords(any()) }
            }
        }

        `when`("워크북이 존재하지 않을 경우") {
            every { workbookDao.selectWorkBook(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { workbookDao.selectWorkBook(any()) }
                verify(exactly = 0) { workbookArticleService.browseWorkbookArticles(any()) }
                verify(exactly = 0) { workbookMemberService.browseWriterRecords(any()) }
            }
        }
    }
})