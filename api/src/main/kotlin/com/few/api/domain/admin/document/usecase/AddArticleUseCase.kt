package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.usecase.dto.AddArticleUseCaseIn
import com.few.api.domain.admin.document.usecase.dto.AddArticleUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.utils.ObjectPathGenerator
import com.few.api.exception.common.ExternalIntegrationException
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.command.InsertFullArticleRecordCommand
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
import java.util.*

@Component
class AddArticleUseCase(
    private val articleDao: ArticleDao,
    private val memberDao: MemberDao,
    private val problemDao: ProblemDao,
    private val documentDao: DocumentDao,
    private val convertDocumentService: ConvertDocumentService,
    private val putDocumentService: PutDocumentService,
    private val getUrlService: GetUrlService,
) {
    @Transactional
    fun execute(useCaseIn: AddArticleUseCaseIn): AddArticleUseCaseOut {
        /** select writerId */
        val writerId = SelectMemberByEmailQuery(useCaseIn.writerEmail).let {
            memberDao.selectMemberByEmail(it)
        } ?: throw NotFoundException("member.notfound.id")

        /**
         * - content type: "md"
         * put origin document to object storage
         * and convert to html source
         * - content type: "html"
         * save html source
         */
        val htmlSource = when {
            useCaseIn.contentType.lowercase(Locale.getDefault()) == "md" -> {
                val mdSource = useCaseIn.contentSource
                val htmlSource = convertDocumentService.mdToHtml(useCaseIn.contentSource)

                val document = runCatching {
                    File.createTempFile("temp", ".md")
                }.onSuccess {
                    it.writeText(mdSource)
                }.getOrThrow()
                val documentName = ObjectPathGenerator.documentPath("md")

                putDocumentService.execute(documentName, document)?.let { res ->
                    val source = res.`object`
                    GetUrlInDto(source).let { query ->
                        getUrlService.execute(query)
                    }.let { dto ->
                        InsertDocumentIfoCommand(
                            path = documentName,
                            url = dto.url
                        ).let { command ->
                            documentDao.insertDocumentIfo(command)
                        }
                        dto.url
                    }
                } ?: throw ExternalIntegrationException("external.putfail.docummet")

                htmlSource
            }
            useCaseIn.contentType.lowercase(Locale.getDefault()) == "html" -> {
                useCaseIn.contentSource
            }
            else -> {
                throw IllegalArgumentException("Unsupported content type: ${useCaseIn.contentType}")
            }
        }

        /** insert article */
        val articleMstId = InsertFullArticleRecordCommand(
            writerId = writerId.memberId,
            mainImageURL = useCaseIn.articleImageUrl,
            title = useCaseIn.title,
            category = CategoryType.convertToCode(useCaseIn.category),
            content = htmlSource
        ).let { articleDao.insertFullArticleRecord(it) }

        /** insert problems */
        useCaseIn.problems.stream().map { problemDatum ->
            InsertProblemsCommand(
                articleId = articleMstId,
                createrId = 0L, // todo fix
                title = problemDatum.title,
                contents = Contents(
                    problemDatum.contents.map { detail ->
                        Content(
                            detail.number,
                            detail.content
                        )
                    }
                ),
                answer = problemDatum.answer,
                explanation = problemDatum.explanation
            )
        }.toList().let { commands ->
            problemDao.insertProblems(commands)
        }

        return AddArticleUseCaseOut(articleMstId)
    }
}