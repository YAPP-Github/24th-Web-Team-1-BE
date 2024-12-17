package com.few.api.domain.workbook.usecase

import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.workbook.repo.WorkbookDao
import com.few.api.domain.workbook.repo.record.SelectWorkBookRecord
import com.few.api.domain.workbook.service.WorkbookArticleService
import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.dto.WorkBookArticleOutDto
import com.few.api.domain.workbook.service.dto.WriterOutDto
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URL
import java.time.LocalDateTime

class ReadWorkbookUseCaseTest :
    BehaviorSpec({
        lateinit var workbookDao: WorkbookDao
        lateinit var workbookArticleService: WorkbookArticleService
        lateinit var workbookMemberService: WorkbookMemberService
        lateinit var useCase: ReadWorkbookUseCase

        beforeContainer {
            workbookDao = mockk<WorkbookDao>()
            workbookArticleService = mockk<WorkbookArticleService>()
            workbookMemberService = mockk<WorkbookMemberService>()
            useCase = ReadWorkbookUseCase(workbookDao, workbookArticleService, workbookMemberService)
        }

        given("특정 워크북 조회 요청이 온 상황에서") {
            val workbookId = 1L
            val useCaseIn = ReadWorkbookUseCaseIn(workbookId = workbookId)

            `when`("워크북이 존재할 경우") {
                val workbookId = 1L
                val title = "workbook title"
                val workBookMainImageUrl = URL("http://localhost:8080/image/main/1")
                val category = CategoryType.ECONOMY.code
                val workbookDescription = "workbook description"
                every { workbookDao.selectWorkBook(any()) } returns
                    SelectWorkBookRecord(
                        id = workbookId,
                        title = title,
                        mainImageUrl = workBookMainImageUrl,
                        category = category,
                        description = workbookDescription,
                        createdAt = LocalDateTime.now(),
                    )

                val articleId = 1L
                val articleWriterId = 1L
                val articleMainImageUrl = URL("http://localhost:8080/image/main/1")
                val articleTitle = "article title"
                val articleContent = "article content"
                every { workbookArticleService.browseWorkbookArticles(any()) } returns
                    listOf(
                        WorkBookArticleOutDto(
                            articleId = articleId,
                            writerId = articleWriterId,
                            mainImageURL = articleMainImageUrl,
                            title = articleTitle,
                            category = category,
                            content = articleContent,
                            createdAt = LocalDateTime.now(),
                        ),
                    )

                val writerName = "writer"
                val writerUrl = URL("http://localhost:8080/writer/1")
                every { workbookMemberService.browseWriterRecords(any()) } returns
                    listOf(
                        WriterOutDto(
                            writerId = articleWriterId,
                            name = writerName,
                            url = writerUrl,
                        ),
                    )

                then("워크북과 관련된 정보를 반환한다") {
                    val useCaseOut = useCase.execute(useCaseIn)
                    useCaseOut.id shouldBe workbookId
                    useCaseOut.title shouldBe title
                    useCaseOut.mainImageUrl shouldBe workBookMainImageUrl
                    useCaseOut.category shouldBe CategoryType.convertToDisplayName(category)
                    useCaseOut.description shouldBe workbookDescription
                    useCaseOut.writers.size shouldBe 1
                    useCaseOut.articles.size shouldBe 1

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