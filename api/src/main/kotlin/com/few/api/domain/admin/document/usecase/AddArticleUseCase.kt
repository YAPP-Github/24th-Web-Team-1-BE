package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.service.ArticleMainCardService
import com.few.api.domain.admin.document.usecase.dto.AddArticleUseCaseIn
import com.few.api.domain.admin.document.usecase.dto.AddArticleUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.service.dto.InitializeArticleMainCardInDto
import com.few.api.domain.admin.document.utils.ObjectPathGenerator
import com.few.api.exception.common.ExternalIntegrationException
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.command.InsertFullArticleRecordCommand
import com.few.api.repo.dao.article.query.ArticleViewCountQuery
import com.few.api.repo.dao.document.DocumentDao
import com.few.api.repo.dao.document.command.InsertDocumentIfoCommand
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.command.InsertProblemsCommand
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import com.few.data.common.code.CategoryType
import com.few.storage.document.service.ConvertDocumentService
import com.few.storage.document.service.PutDocumentService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Component
class AddArticleUseCase(
    private val articleDao: ArticleDao,
    private val memberDao: MemberDao,
    private val problemDao: ProblemDao,
    private val documentDao: DocumentDao,
    private val articleViewCountDao: ArticleViewCountDao,
    private val convertDocumentService: ConvertDocumentService,
    private val putDocumentService: PutDocumentService,
    private val getUrlService: GetUrlService,
    private val articleMainCardService: ArticleMainCardService,
) {
    @Transactional
    fun execute(useCaseIn: AddArticleUseCaseIn): AddArticleUseCaseOut {
        val writerRecord = memberDao.selectMemberByEmail(SelectMemberByEmailQuery(useCaseIn.writerEmail))
            ?: throw NotFoundException("member.notfound.id")

        val htmlSource = when (useCaseIn.contentType.lowercase(Locale.getDefault())) {
            "md" -> {
                val mdSource = useCaseIn.contentSource
                val htmlSource = convertDocumentService.mdToHtml(mdSource)

                val document = File.createTempFile("temp", ".md").apply {
                    writeText(mdSource)
                }
                val documentName = ObjectPathGenerator.documentPath("md")

                val url = putDocumentService.execute(documentName, document)?.let { res ->
                    val source = res.`object`
                    getUrlService.execute(GetUrlInDto(source)).url
                } ?: throw ExternalIntegrationException("external.putfail.document")

                documentDao.insertDocumentIfo(InsertDocumentIfoCommand(path = documentName, url = url))
                htmlSource
            }
            "html" -> useCaseIn.contentSource
            else -> throw IllegalArgumentException("Unsupported content type: ${useCaseIn.contentType}")
        }

        val category = CategoryType.fromName(useCaseIn.category)
            ?: throw NotFoundException("article.invalid.category")

        val articleMstId = articleDao.insertFullArticleRecord(
            InsertFullArticleRecordCommand(
                writerId = writerRecord.memberId,
                mainImageURL = useCaseIn.articleImageUrl,
                title = useCaseIn.title,
                category = category.code,
                content = htmlSource
            )
        )

        useCaseIn.problems.map { problemDatum ->
            InsertProblemsCommand(
                articleId = articleMstId,
                createrId = 0L, // todo fix
                title = problemDatum.title,
                contents = Contents(
                    problemDatum.contents.map { detail ->
                        Content(detail.number, detail.content)
                    }
                ),
                answer = problemDatum.answer,
                explanation = problemDatum.explanation
            )
        }.also { commands ->
            problemDao.insertProblems(commands)
        }

        articleViewCountDao.insertArticleViewCountToZero(
            ArticleViewCountQuery(articleMstId, category)
        )

        articleMainCardService.initialize(
            InitializeArticleMainCardInDto(
                articleId = articleMstId,
                articleTitle = useCaseIn.title,
                mainImageUrl = useCaseIn.articleImageUrl,
                categoryCd = category.code,
                createdAt = LocalDateTime.now(), // TODO: DB insert 시점으로 변경
                writerId = writerRecord.memberId,
                writerEmail = useCaseIn.writerEmail,
                writerName = writerRecord.writerName ?: throw NotFoundException("article.writer.name"),
                writerUrl = writerRecord.url ?: throw NotFoundException("article.writer.url"),
                writerImgUrl = writerRecord.imageUrl ?: throw NotFoundException("article.writer.url")
            )
        )

        return AddArticleUseCaseOut(articleMstId)
    }
}