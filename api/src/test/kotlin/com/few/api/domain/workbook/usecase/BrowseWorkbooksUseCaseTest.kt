package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.WorkbookSubscribeService
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.domain.workbook.service.dto.WriterMappedWorkbookOutDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.domain.workbook.usecase.model.AuthMainViewWorkbookOrderDelegator
import com.few.api.domain.workbook.usecase.model.BasicWorkbookOrderDelegator
import com.few.api.domain.workbook.usecase.service.WorkbookOrderDelegatorExecutor
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecordWithSubscriptionCount
import com.few.api.web.support.ViewCategory
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
    lateinit var workbookSubscribeService: WorkbookSubscribeService
    lateinit var workbookOrderDelegatorExecutor: WorkbookOrderDelegatorExecutor
    lateinit var useCase: BrowseWorkbooksUseCase

    beforeContainer {
        workbookDao = mockk<WorkbookDao>()
        workbookMemberService = mockk<WorkbookMemberService>()
        workbookSubscribeService = mockk<WorkbookSubscribeService>()
        workbookOrderDelegatorExecutor = mockk<WorkbookOrderDelegatorExecutor>()
        useCase =
            BrowseWorkbooksUseCase(workbookDao, workbookMemberService, workbookSubscribeService, workbookOrderDelegatorExecutor)
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

            every {
                workbookOrderDelegatorExecutor.execute(any())
            } returns listOf(
                BrowseWorkBookDetail(
                    id = 1L,
                    title = "workbook title",
                    mainImageUrl = URL("https://jh-labs.tistory.com/"),
                    category = WorkBookCategory.ECONOMY.displayName,
                    description = "workbook description",
                    createdAt = LocalDateTime.now(),
                    writerDetails = listOf(
                        WriterDetail(
                            id = 1L,
                            name = "hunca",
                            url = URL("https://jh-labs.tistory.com/")
                        )
                    ),
                    subscriptionCount = 10
                )
            )

            then("지정한 카테고리의 워크북이 조회된다") {
                val useCaseIn = BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = null, memberId = null)
                useCase.execute(useCaseIn)

                verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                verify(exactly = 0) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                verify(exactly = 1) { workbookOrderDelegatorExecutor.execute(any()) }
            }
        }
    }

    given("메인에서 다수 워크북 조회 요청이 온 상황에서") {
        `when`("로그인이 되어 있을 경우") {
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

            every {
                workbookSubscribeService.browseMemberSubscribeWorkbooks(any())
            } returns listOf(
                BrowseMemberSubscribeWorkbooksOutDto(
                    workbookId = 1L,
                    isActiveSub = true,
                    currentDay = 1
                ),
                BrowseMemberSubscribeWorkbooksOutDto(
                    workbookId = 2L,
                    isActiveSub = false,
                    currentDay = 1
                )
            )

            every {
                workbookOrderDelegatorExecutor.execute(any(AuthMainViewWorkbookOrderDelegator::class))
            } returns listOf(
                BrowseWorkBookDetail(
                    id = 1L,
                    title = "workbook title",
                    mainImageUrl = URL("https://jh-labs.tistory.com/"),
                    category = WorkBookCategory.ECONOMY.displayName,
                    description = "workbook description",
                    createdAt = LocalDateTime.now(),
                    writerDetails = listOf(
                        WriterDetail(
                            id = 1L,
                            name = "hunca",
                            url = URL("https://jh-labs.tistory.com/")
                        )
                    ),
                    subscriptionCount = 10
                )
            )

            then("인증 메인뷰 워크북 정렬이 실행된 결과가 반환된다") {
                val useCaseIn = BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = ViewCategory.MAIN_CARD, memberId = 1L)
                useCase.execute(useCaseIn)

                verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                verify(exactly = 1) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                verify(exactly = 1) { workbookOrderDelegatorExecutor.execute(any(AuthMainViewWorkbookOrderDelegator::class)) }
            }
        }

        `when`("로그인이 되어 있지 않은 경우") {
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

            every {
                workbookOrderDelegatorExecutor.execute(any(BasicWorkbookOrderDelegator::class))
            } returns listOf(
                BrowseWorkBookDetail(
                    id = 1L,
                    title = "workbook title",
                    mainImageUrl = URL("https://jh-labs.tistory.com/"),
                    category = WorkBookCategory.ECONOMY.displayName,
                    description = "workbook description",
                    createdAt = LocalDateTime.now(),
                    writerDetails = listOf(
                        WriterDetail(
                            id = 1L,
                            name = "hunca",
                            url = URL("https://jh-labs.tistory.com/")
                        )
                    ),
                    subscriptionCount = 10
                )
            )

            then("지정한 카테고리의 워크북이 조회된다") {
                val useCaseIn = BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = ViewCategory.MAIN_CARD, memberId = null)
                useCase.execute(useCaseIn)

                verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                verify(exactly = 0) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                verify(exactly = 1) { workbookOrderDelegatorExecutor.execute(any(BasicWorkbookOrderDelegator::class)) }
            }
        }
    }
})