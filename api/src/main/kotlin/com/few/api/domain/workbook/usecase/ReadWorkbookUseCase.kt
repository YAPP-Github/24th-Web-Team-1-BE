package com.few.api.domain.workbook.usecase

import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseOut
import com.few.api.domain.workbook.service.*
import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesInDto
import com.few.api.domain.workbook.service.dto.BrowseWriterRecordsInDto
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.workbook.repo.WorkbookDao
import com.few.api.domain.workbook.repo.query.SelectWorkBookRecordQuery
import com.few.api.domain.workbook.usecase.dto.ArticleDetail
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import org.springframework.stereotype.Component

@Component
class ReadWorkbookUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookArticleService: WorkbookArticleService,
    private val workbookMemberService: WorkbookMemberService,
) {

    fun execute(useCaseIn: ReadWorkbookUseCaseIn): ReadWorkbookUseCaseOut {
        val workbookId = useCaseIn.workbookId

        val workbookRecord = workbookDao.selectWorkBook(SelectWorkBookRecordQuery(workbookId))
            ?: throw NotFoundException("workbook.notfound.id")

        val workbookMappedArticles =
            workbookArticleService.browseWorkbookArticles(BrowseWorkbookArticlesInDto(workbookId))

        val writerRecords = workbookMemberService.browseWriterRecords(
            BrowseWriterRecordsInDto(
                workbookMappedArticles.writerIds()
            )
        )

        return ReadWorkbookUseCaseOut(
            id = workbookRecord.id,
            mainImageUrl = workbookRecord.mainImageUrl,
            title = workbookRecord.title,
            description = workbookRecord.description,
            category = CategoryType.convertToDisplayName(workbookRecord.category),
            createdAt = workbookRecord.createdAt,
            writers = writerRecords.toWriterDetails().map {
                WriterDetail(
                    id = it.writerId,
                    name = it.name,
                    url = it.url
                )
            },
            articles = workbookMappedArticles.toArticleDetails().map {
                ArticleDetail(
                    id = it.articleId,
                    title = it.title
                )
            }
        )
    }
}