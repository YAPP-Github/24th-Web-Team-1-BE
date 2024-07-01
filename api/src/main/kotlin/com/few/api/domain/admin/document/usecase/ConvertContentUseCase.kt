package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.dto.ConvertContentUseCaseIn
import com.few.api.domain.admin.document.dto.ConvertContentUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlQuery

import com.few.api.repo.dao.document.DocumentDao
import com.few.api.repo.dao.document.command.InsertDocumentIfoCommand
import com.few.storage.document.service.ConvertDocumentService
import com.few.storage.document.service.PutDocumentService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.time.LocalDate
import kotlin.random.Random

@Component
class ConvertContentUseCase(
    private val documentDao: DocumentDao,
    private val convertDocumentService: ConvertDocumentService,
    private val putDocumentService: PutDocumentService,
    private val getUrlService: GetUrlService
) {
    @Transactional
    fun execute(useCaseIn: ConvertContentUseCaseIn): ConvertContentUseCaseOut {
        val dateDir = LocalDate.now().toString()
        val contentSource = useCaseIn.content

        val documentSuffix = contentSource.originalFilename?.substringAfterLast(".") ?: "md"
        val document = runCatching {
            File.createTempFile("temp", ".$documentSuffix")
        }.onSuccess {
            contentSource.transferTo(it)
        }.getOrThrow()
        val documentName = "documents/$dateDir/${generateRandomName()}" + ".$documentSuffix"

        val originDownloadUrl = putDocumentService.execute(documentName, document)?.let { res ->
            val source = res.`object`
            GetUrlQuery(source).let { query ->
                getUrlService.execute(query)
            }.let { url ->
                InsertDocumentIfoCommand(
                    path = documentName,
                    url = url
                ).let { command ->
                    documentDao.insertDocumentIfo(command)
                }
                url
            }
        } ?: throw IllegalStateException("Failed to put document")

        val html =
            convertDocumentService.mdToHtml(document.readBytes().toString(Charsets.UTF_8))

        return ConvertContentUseCaseOut(html.replace("\n", "<br>"), originDownloadUrl)
    }

    private fun generateRandomName(): String {
        return randomString()
    }

    private fun randomString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..16)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }
}