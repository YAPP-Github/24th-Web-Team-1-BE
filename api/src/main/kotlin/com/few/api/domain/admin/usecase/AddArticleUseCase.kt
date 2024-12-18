package com.few.api.domain.admin.usecase

import com.few.api.domain.admin.repo.document.DocumentDao
import com.few.api.domain.admin.repo.document.command.InsertDocumentIfoCommand
import com.few.api.domain.admin.service.AdminArticleMainCardService
import com.few.api.domain.admin.service.GetUrlService
import com.few.api.domain.admin.service.dto.GetUrlInDto
import com.few.api.domain.admin.service.dto.InitializeArticleMainCardInDto
import com.few.api.domain.admin.usecase.dto.AddArticleUseCaseIn
import com.few.api.domain.admin.usecase.dto.AddArticleUseCaseOut
import com.few.api.domain.admin.utils.ObjectPathGenerator
import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.ArticleViewCountDao
import com.few.api.domain.article.repo.command.InsertFullArticleRecordCommand
import com.few.api.domain.article.repo.query.ArticleViewCountQuery
import com.few.api.domain.common.exception.ExternalIntegrationException
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.query.SelectMemberByEmailQuery
import com.few.api.domain.problem.repo.ProblemDao
import com.few.api.domain.problem.repo.command.InsertProblemsCommand
import com.few.api.domain.problem.repo.support.Content
import com.few.api.domain.problem.repo.support.Contents
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional
import storage.document.PutDocumentProvider
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
    private val convertDocumentService: com.few.api.domain.admin.service.ConvertDocumentService,
    private val putDocumentService: PutDocumentProvider,
    private val getUrlService: GetUrlService,
    private val adminArticleMainCardService: AdminArticleMainCardService,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: AddArticleUseCaseIn): AddArticleUseCaseOut {
        val writerRecord =
            memberDao.selectMemberByEmail(SelectMemberByEmailQuery(useCaseIn.writerEmail))
                ?: throw NotFoundException("member.notfound.id")

        val htmlSource =
            when (useCaseIn.contentType.lowercase(Locale.getDefault())) {
                "md" -> {
                    val mdSource = useCaseIn.contentSource
                    val htmlSource = convertDocumentService.mdToHtml(mdSource)

                    val document =
                        File.createTempFile("temp", ".md").apply {
                            writeText(mdSource)
                        }
                    val documentName = ObjectPathGenerator.documentPath("md")

                    val url =
                        putDocumentService.execute(documentName, document)?.let { res ->
                            val source = res.`object`
                            getUrlService.execute(GetUrlInDto(source)).url
                        } ?: throw ExternalIntegrationException("external.putfail.document")

                    documentDao.insertDocumentIfo(InsertDocumentIfoCommand(path = documentName, url = url))
                    htmlSource
                }
                "html" -> useCaseIn.contentSource
                else -> throw IllegalArgumentException("Unsupported content type: ${useCaseIn.contentType}")
            }

        val category =
            CategoryType.fromName(useCaseIn.category)
                ?: throw NotFoundException("article.invalid.category")

        val articleMstId =
            articleDao.insertFullArticleRecord(
                InsertFullArticleRecordCommand(
                    writerId = writerRecord.memberId,
                    mainImageURL = useCaseIn.articleImageUrl,
                    title = useCaseIn.title,
                    category = category.code,
                    content = htmlSource,
                ),
            )

        useCaseIn.problems
            .map { problemDatum ->
                InsertProblemsCommand(
                    articleId = articleMstId,
                    createrId = 0L, // todo fix
                    title = problemDatum.title,
                    contents =
                        Contents(
                            problemDatum.contents.map { detail ->
                                Content(detail.number, detail.content)
                            },
                        ),
                    answer = problemDatum.answer,
                    explanation = problemDatum.explanation,
                )
            }.also { commands ->
                problemDao.insertProblems(commands)
            }

        articleViewCountDao.insertArticleViewCountToZero(
            ArticleViewCountQuery(articleMstId, category),
        )

        adminArticleMainCardService.initialize(
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
                writerImgUrl = writerRecord.imageUrl ?: throw NotFoundException("article.writer.url"),
            ),
        )

        return AddArticleUseCaseOut(articleMstId)
    }
}