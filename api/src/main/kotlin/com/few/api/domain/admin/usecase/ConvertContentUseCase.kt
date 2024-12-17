package com.few.api.domain.admin.usecase

import com.few.api.domain.admin.repo.document.DocumentDao
import com.few.api.domain.admin.repo.document.command.InsertDocumentIfoCommand
import com.few.api.domain.admin.service.GetUrlService
import com.few.api.domain.admin.service.dto.GetUrlInDto
import com.few.api.domain.admin.usecase.dto.ConvertContentUseCaseIn
import com.few.api.domain.admin.usecase.dto.ConvertContentUseCaseOut
import com.few.api.domain.admin.utils.ObjectPathGenerator
import com.few.api.domain.common.exception.ExternalIntegrationException
import com.few.api.domain.common.exception.InsertException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import storage.document.PutDocumentProvider
import java.io.File

@Component
class ConvertContentUseCase(
    private val documentDao: DocumentDao,
    private val convertDocumentService: com.few.api.domain.admin.service.ConvertDocumentService,
    private val putDocumentService: PutDocumentProvider,
    private val getUrlService: GetUrlService,
) {
    @Transactional
    fun execute(useCaseIn: ConvertContentUseCaseIn): ConvertContentUseCaseOut {
        val contentSource = useCaseIn.content

        val documentSuffix = contentSource.originalFilename?.substringAfterLast(".") ?: "md"
        val document =
            File.createTempFile("temp", ".$documentSuffix").apply {
                contentSource.transferTo(this)
            }

        val documentName = ObjectPathGenerator.documentPath(documentSuffix)

        val originDownloadUrl =
            putDocumentService
                .execute(documentName, document)
                ?.`object`
                ?.let { source ->
                    getUrlService
                        .execute(GetUrlInDto(source))
                        .also { dto ->
                            documentDao.insertDocumentIfo(
                                InsertDocumentIfoCommand(
                                    path = documentName,
                                    url = dto.url,
                                ),
                            ) ?: throw InsertException("document.insertfail.record")
                        }.let { savedDocument ->
                            savedDocument.url
                        }
                } ?: throw ExternalIntegrationException("external.document.presignedfail")

        val html = convertDocumentService.mdToHtml(document.readBytes().toString(Charsets.UTF_8))

        return ConvertContentUseCaseOut(html.replace("\n", "<br>"), originDownloadUrl)
    }
}