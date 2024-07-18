package com.few.api.domain.article.usecase

import com.few.api.domain.article.event.ArticleViewHisAsyncEvent
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemsOutDto
import com.few.api.domain.article.service.dto.ReadWriterOutDto
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.record.SelectArticleRecord
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*

import java.net.URL
import java.time.LocalDateTime

class ReadArticleUseCaseTest : BehaviorSpec({
    val log = KotlinLogging.logger {}

    lateinit var articleDao: ArticleDao
    lateinit var readArticleWriterRecordService: ReadArticleWriterRecordService
    lateinit var browseArticleProblemsService: BrowseArticleProblemsService
    lateinit var useCase: ReadArticleUseCase
    lateinit var articleViewHisDao: ArticleViewHisDao
    lateinit var articleViewHisAsyncEvent: ArticleViewHisAsyncEvent
    val useCaseIn = ReadArticleUseCaseIn(articleId = 1L, memberId = 1L)

    beforeContainer {
        articleDao = mockk<ArticleDao>()
        readArticleWriterRecordService = mockk<ReadArticleWriterRecordService>()
        browseArticleProblemsService = mockk<BrowseArticleProblemsService>()
        articleViewHisDao = mockk<ArticleViewHisDao>()
        articleViewHisAsyncEvent = mockk<ArticleViewHisAsyncEvent>()
        useCase = ReadArticleUseCase(
            articleDao,
            readArticleWriterRecordService,
            browseArticleProblemsService,
            articleViewHisDao,
            articleViewHisAsyncEvent
        )
    }

    given("아티클 조회 요청이 온 상황에서") {
        `when`("아티클과 작가가 존재할 경우") {
            val record = SelectArticleRecord(
                articleId = 1L,
                writerId = 1L,
                mainImageURL = URL("https://jh-labs.tistory.com/"),
                title = "title",
                category = (10).toByte(),
                content = "content",
                createdAt = LocalDateTime.now()
            )
            val writerSvcOutDto = ReadWriterOutDto(
                writerId = 1L,
                name = "hunca",
                url = URL("https://jh-labs.tistory.com/")
            )
            val probSvcOutDto = BrowseArticleProblemsOutDto(problemIds = listOf(1, 2, 3))

            every { articleDao.selectArticleRecord(any()) } returns record
            every { readArticleWriterRecordService.execute(any()) } returns writerSvcOutDto
            every { browseArticleProblemsService.execute(any()) } returns probSvcOutDto
            every { articleViewHisDao.countArticleViews(any()) } returns 1L
            every { articleViewHisAsyncEvent.addArticleViewHis(any(), any()) } answers {
                log.debug { "Inserting article view history asynchronously" }
            }

            then("아티클이 정상 조회된다") {
                useCase.execute(useCaseIn)

                verify(exactly = 1) { articleDao.selectArticleRecord(any()) }
                verify(exactly = 1) { readArticleWriterRecordService.execute(any()) }
                verify(exactly = 1) { browseArticleProblemsService.execute(any()) }
                verify(exactly = 1) { articleViewHisDao.countArticleViews(any()) }
                verify(exactly = 1) { articleViewHisAsyncEvent.addArticleViewHis(any(), any()) }
            }
        }

        `when`("존재하지 않는 아티클일 경우") {
            every { articleDao.selectArticleRecord(any()) } returns null

            then("예외가 발생한다") {
                shouldThrow<Exception> { useCase.execute(useCaseIn) }

                verify(exactly = 1) { articleDao.selectArticleRecord(any()) }
            }
        }
    }
})