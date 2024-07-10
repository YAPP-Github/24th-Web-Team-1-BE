package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.usecase.dto.ConvertContentUseCaseIn
import com.few.api.domain.admin.document.usecase.dto.ConvertContentUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.utils.ObjectPathGenerator
import com.few.api.exception.common.ExternalIntegrationException
import com.few.api.exception.common.InsertException

import com.few.api.repo.dao.document.DocumentDao
import com.few.api.repo.dao.document.command.InsertDocumentIfoCommand
import com.few.storage.document.service.ConvertDocumentService
import com.few.storage.document.service.PutDocumentService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File

@Component
class ConvertContentUseCase(
    private val documentDao: DocumentDao,
    private val convertDocumentService: ConvertDocumentService,
    private val putDocumentService: PutDocumentService,
    private val getUrlService: GetUrlService
) {
    @Transactional
    fun execute(useCaseIn: ConvertContentUseCaseIn): ConvertContentUseCaseOut {
        val contentSource = useCaseIn.content

        val documentSuffix = contentSource.originalFilename?.substringAfterLast(".") ?: "md"
        val document = runCatching {
            File.createTempFile("temp", ".$documentSuffix")
        }.onSuccess {
            contentSource.transferTo(it)
        }.getOrThrow()
        val documentName = ObjectPathGenerator.documentPath(documentSuffix)

        val originDownloadUrl = putDocumentService.execute(documentName, document)?.let { res ->
            val source = res.`object`
            GetUrlInDto(source).let { query ->
                getUrlService.execute(query)
            }.let { dto ->
                InsertDocumentIfoCommand(
                    path = documentName,
                    url = dto.url
                ).let { command ->
                    documentDao.insertDocumentIfo(command) ?: throw InsertException("document.insertfail.record")
                }
                dto.url
            }
        } ?: throw ExternalIntegrationException("external.document.presignedfail")

        val html =
            convertDocumentService.mdToHtml(document.readBytes().toString(Charsets.UTF_8))

        return ConvertContentUseCaseOut(html.replace("\n", "<br>"), originDownloadUrl)
    }
}