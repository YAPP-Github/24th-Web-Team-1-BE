package com.few.api.domain.workbook.usecase

import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.common.vo.ViewCategory
import com.few.api.domain.common.vo.WorkBookCategory
import com.few.api.domain.workbook.repo.WorkbookDao
import com.few.api.domain.workbook.repo.record.SelectWorkBookRecordWithSubscriptionCount
import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.WorkbookSubscribeService
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.domain.workbook.service.dto.WriterMappedWorkbookOutDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URL
import java.time.LocalDateTime
import java.util.stream.IntStream

class BrowseWorkbooksUseCaseTest :
    BehaviorSpec({
        lateinit var workbookDao: WorkbookDao
        lateinit var workbookMemberService: WorkbookMemberService
        lateinit var workbookSubscribeService: WorkbookSubscribeService
        lateinit var useCase: BrowseWorkbooksUseCase

        beforeContainer {
            workbookDao = mockk<WorkbookDao>()
            workbookMemberService = mockk<WorkbookMemberService>()
            workbookSubscribeService = mockk<WorkbookSubscribeService>()
            useCase =
                BrowseWorkbooksUseCase(workbookDao, workbookMemberService, workbookSubscribeService)
        }

        given("메인 뷰에서 로그인 된 상태로 카테고리를 지정하여 다수 워크북 조회 요청이 온 상황에서") {
            val memberId = 1L
            val useCaseIn =
                BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = ViewCategory.MAIN_CARD, memberId = memberId)

            `when`("특정 카테고리로 지정되어 있을 경우") {
                val workbookCount = 2
                every { workbookDao.browseWorkBookWithSubscriptionCount(any()) } returns
                    IntStream
                        .range(1, 1 + workbookCount)
                        .mapToObj {
                            SelectWorkBookRecordWithSubscriptionCount(
                                id = it.toLong(),
                                title = "workbook title$it",
                                mainImageUrl = URL("http://localhost:8080/image/main/$it"),
                                category = CategoryType.ECONOMY.code,
                                description = "workbook$it description",
                                createdAt = LocalDateTime.now(),
                                subscriptionCount = it.toLong(),
                            )
                        }.toList()

                every { workbookMemberService.browseWorkbookWriterRecords(any()) } returns
                    mapOf(
                        1L to
                            listOf(
                                WriterMappedWorkbookOutDto(
                                    writerId = 1L,
                                    name = "writer1",
                                    url = URL("http://localhost:8080/image/writer/1"),
                                    workbookId = 1L,
                                ),
                            ),
                        2L to
                            listOf(
                                WriterMappedWorkbookOutDto(
                                    writerId = 2L,
                                    name = "writer2",
                                    url = URL("http://localhost:8080/image/writer/2"),
                                    workbookId = 2L,
                                ),
                            ),
                    )

                every {
                    workbookSubscribeService.browseMemberSubscribeWorkbooks(any())
                } returns
                    listOf(
                        BrowseMemberSubscribeWorkbooksOutDto(
                            workbookId = 1L,
                            isActiveSub = true,
                            currentDay = 1,
                        ),
                        BrowseMemberSubscribeWorkbooksOutDto(
                            workbookId = 2L,
                            isActiveSub = false,
                            currentDay = 1,
                        ),
                    )

                then("경제 카테고리의 워크북이 조회된다") {
                    val useCaseOut = useCase.execute(useCaseIn)
                    useCaseOut.workbooks.size shouldBe workbookCount

                    useCaseOut.workbooks.forEachIndexed { index, browseWorkBookDetail ->
                        browseWorkBookDetail.id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.title shouldBe "workbook title${index + 1}"
                        browseWorkBookDetail.mainImageUrl shouldBe URL("http://localhost:8080/image/main/${index + 1}")
                        browseWorkBookDetail.category shouldBe WorkBookCategory.ECONOMY.displayName
                        browseWorkBookDetail.description shouldBe "workbook${index + 1} description"
                        browseWorkBookDetail.writerDetails.size shouldBe 1
                        browseWorkBookDetail.writerDetails[0].id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.writerDetails[0].name shouldBe "writer${index + 1}"
                        browseWorkBookDetail.writerDetails[0].url shouldBe URL("http://localhost:8080/image/writer/${index + 1}")
                        browseWorkBookDetail.subscriptionCount shouldBe (index + 1).toLong()
                    }

                    verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                    verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                    verify(exactly = 1) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                }
            }
        }

        given("메인 뷰에서 로그인 안된 상태로 카테고리를 지정하여 다수 워크북 조회 요청이 온 상황에서") {
            val useCaseIn =
                BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = ViewCategory.MAIN_CARD, memberId = null)

            `when`("특정 카테고리로 지정되어 있을 경우") {
                val workbookCount = 2
                every { workbookDao.browseWorkBookWithSubscriptionCount(any()) } returns
                    IntStream
                        .range(1, 1 + workbookCount)
                        .mapToObj {
                            SelectWorkBookRecordWithSubscriptionCount(
                                id = it.toLong(),
                                title = "workbook title$it",
                                mainImageUrl = URL("http://localhost:8080/image/main/$it"),
                                category = CategoryType.ECONOMY.code,
                                description = "workbook$it description",
                                createdAt = LocalDateTime.now(),
                                subscriptionCount = it.toLong(),
                            )
                        }.toList()

                val workbookWriterRecords = HashMap<Long, List<WriterMappedWorkbookOutDto>>()
                for (i in 1..workbookCount) {
                    workbookWriterRecords[i.toLong()] =
                        listOf(
                            WriterMappedWorkbookOutDto(
                                writerId = i.toLong(),
                                name = "writer$i",
                                url = URL("http://localhost:8080/image/writer/$i"),
                                workbookId = i.toLong(),
                            ),
                        )
                }
                every { workbookMemberService.browseWorkbookWriterRecords(any()) } returns workbookWriterRecords

                then("경제 카테고리의 워크북이 조회된다") {
                    val useCaseOut = useCase.execute(useCaseIn)
                    useCaseOut.workbooks.size shouldBe workbookCount

                    useCaseOut.workbooks.forEachIndexed { index, browseWorkBookDetail ->
                        browseWorkBookDetail.id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.title shouldBe "workbook title${index + 1}"
                        browseWorkBookDetail.mainImageUrl shouldBe URL("http://localhost:8080/image/main/${index + 1}")
                        browseWorkBookDetail.category shouldBe WorkBookCategory.ECONOMY.displayName
                        browseWorkBookDetail.description shouldBe "workbook${index + 1} description"
                        browseWorkBookDetail.writerDetails.size shouldBe 1
                        browseWorkBookDetail.writerDetails[0].id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.writerDetails[0].name shouldBe "writer${index + 1}"
                        browseWorkBookDetail.writerDetails[0].url shouldBe URL("http://localhost:8080/image/writer/${index + 1}")
                        browseWorkBookDetail.subscriptionCount shouldBe (index + 1).toLong()
                    }

                    verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                    verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                    verify(exactly = 0) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                }
            }
        }

        given("메인 뷰가 아닌 상태로 카테고리를 지정하여 다수 워크북 조회 요청이 온 상황에서") {
            val useCaseIn = BrowseWorkbooksUseCaseIn(category = WorkBookCategory.ECONOMY, viewCategory = null, memberId = null)

            val workbookCount = 2
            `when`("특정 카테고리로 지정되어 있을 경우") {
                every { workbookDao.browseWorkBookWithSubscriptionCount(any()) } returns
                    IntStream
                        .range(
                            1,
                            1 + workbookCount,
                        ).mapToObj {
                            SelectWorkBookRecordWithSubscriptionCount(
                                id = it.toLong(),
                                title = "workbook title$it",
                                mainImageUrl = URL("http://localhost:8080/image/main/$it"),
                                category = CategoryType.ECONOMY.code,
                                description = "workbook$it description",
                                createdAt = LocalDateTime.now(),
                                subscriptionCount = it.toLong(),
                            )
                        }.toList()

                every { workbookMemberService.browseWorkbookWriterRecords(any()) } returns
                    mapOf(
                        1L to
                            listOf(
                                WriterMappedWorkbookOutDto(
                                    writerId = 1L,
                                    name = "writer1",
                                    url = URL("http://localhost:8080/image/writer/1"),
                                    workbookId = 1L,
                                ),
                            ),
                        2L to
                            listOf(
                                WriterMappedWorkbookOutDto(
                                    writerId = 2L,
                                    name = "writer2",
                                    url = URL("http://localhost:8080/image/writer/2"),
                                    workbookId = 2L,
                                ),
                            ),
                    )

                then("경제 카테고리의 워크북이 조회된다") {
                    val useCaseOut = useCase.execute(useCaseIn)
                    useCaseOut.workbooks.size shouldBe workbookCount
                    useCaseOut.workbooks.forEachIndexed { index, browseWorkBookDetail ->
                        browseWorkBookDetail.id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.title shouldBe "workbook title${index + 1}"
                        browseWorkBookDetail.mainImageUrl shouldBe URL("http://localhost:8080/image/main/${index + 1}")
                        browseWorkBookDetail.category shouldBe WorkBookCategory.ECONOMY.displayName
                        browseWorkBookDetail.description shouldBe "workbook${index + 1} description"
                        browseWorkBookDetail.writerDetails.size shouldBe 1
                        browseWorkBookDetail.writerDetails[0].id shouldBe (index + 1).toLong()
                        browseWorkBookDetail.writerDetails[0].name shouldBe "writer${index + 1}"
                        browseWorkBookDetail.writerDetails[0].url shouldBe URL("http://localhost:8080/image/writer/${index + 1}")
                        browseWorkBookDetail.subscriptionCount shouldBe (index + 1).toLong()
                    }

                    verify(exactly = 1) { workbookDao.browseWorkBookWithSubscriptionCount(any()) }
                    verify(exactly = 1) { workbookMemberService.browseWorkbookWriterRecords(any()) }
                    verify(exactly = 0) { workbookSubscribeService.browseMemberSubscribeWorkbooks(any()) }
                }
            }
        }
    })