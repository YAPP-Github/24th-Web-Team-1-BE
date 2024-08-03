package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.dto.WriterMappedWorkbookOutDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecordWithSubscriptionCount
import com.few.api.web.support.WorkBookCategory
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

import java.net.URL
import java.time.LocalDateTime

class BrowseWorkbooksUseCaseTest : BehaviorSpec({
    lateinit var workbookDao: WorkbookDao
    lateinit var workbookMemberService: WorkbookMemberService
    lateinit var useCase: BrowseWorkbooksUseCase

    beforeContainer {
        workbookDao = mockk<WorkbookDao>()
        workbookMemberService = mockk<WorkbookMemberService>()
        useCase = BrowseWorkbooksUseCase(workbookDao, workbookMemberService)
    }

    given("다수 워크북 조회 요청이 온 상황에서") {
        `when`("카테고리가 지정되어 있을 경우") {
            every { workbookDao.browseWorkBookWithSubscriptionCount(any()) } returns listOf(
                SelectWorkBookRecordWithSubscriptionCount(
                    id = 1L,
                    title = "workbook title",
                    mainImageUrl = URL("https://jh-labs.tistory.com/"),
                    category = (10).toByte(),
                    description = "workbook description",
                    createdAt = LocalDateTime.now(),
                    subscriptionCount = 10
                ),
                SelectWorkBookRecordWithSubscriptionCount(
                    id = 2L,
                    title = "workbook title",
                    mainImageUrl = URL("https://jh-labs.tistory.com/"),
                    category = (10).toByte(),
                    description = "workbook description",
                    createdAt = LocalDateTime.now(),
                    subscriptionCount = 10
                )
            )

            every { workbookMemberService.browseWorkbookWriterRecords(any()) } returns mapOf(
                1L to listOf(
                    WriterMappedWorkbookOutDto(
                        writerId = 1L,
                        name = "hunca",
                        url = URL("https://jh-labs.tistory.com/"),
                        workbookId = 1L
                    )
                ),
                2L to listOf(
                    WriterMappedWorkbookOutDto(
                        writerId = 2L,
                        name = "hunca",
                        url = URL("https://jh-labs.tistory.com/"),
                        workbookId = 2L
                    )
                )
            )

            then("지정한 카테고리의 워크북이 조회된다") {
                val useCaseIn = BrowseWorkbooksUseCaseIn(category = WorkBookCategory.All)
                useCase.execute(useCaseIn)

                verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
            }
        }
    }
})