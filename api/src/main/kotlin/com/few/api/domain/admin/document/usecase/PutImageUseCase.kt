package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.usecase.dto.PutImageUseCaseIn
import com.few.api.domain.admin.document.usecase.dto.PutImageUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlInDto
import com.few.api.domain.admin.document.utils.ObjectPathGenerator
import com.few.api.exception.common.ExternalIntegrationException
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.image.ImageDao
import com.few.api.repo.dao.image.command.InsertImageIfoCommand
import com.few.storage.image.service.PutImageService
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File

@Component
class PutImageUseCase(
    private val imageDao: ImageDao,
    private val putImageService: PutImageService,
    private val getUrlService: GetUrlService,
) {

    @Transactional
    fun execute(useCaseIn: PutImageUseCaseIn): PutImageUseCaseOut {
        val imageSource = useCaseIn.source
        val suffix = imageSource.originalFilename?.substringAfterLast(".") ?: "jpg"

        val imageName = ObjectPathGenerator.imagePath(suffix)
        val originImage = runCatching {
            File.createTempFile("temp", ".$suffix")
        }.onSuccess {
            imageSource.transferTo(it)
        }.getOrThrow()

        val webpImage = ImmutableImage.loader().fromFile(originImage)
            .output(WebpWriter.DEFAULT, File.createTempFile("temp", ".webp"))

        val url = putImageService.execute(imageName, originImage)?.let { res ->
            val source = res.`object`
            GetUrlInDto(source).let { query ->
                getUrlService.execute(query)
            }.let { dto ->
                InsertImageIfoCommand(source, dto.url).let { command ->
                    imageDao.insertImageIfo(command) ?: throw InsertException("image.insertfail.record")
                }
                return@let dto.url
            }
        } ?: throw ExternalIntegrationException("external.presignedfail.image")

        val webpUrl =
            putImageService.execute(imageName.replaceAfterLast(".", "webp"), webpImage)?.let { res ->
                val source = res.`object`
                GetUrlInDto(source).let { query ->
                    getUrlService.execute(query)
                }.let { dto ->
                    InsertImageIfoCommand(source, dto.url).let { command ->
                        imageDao.insertImageIfo(command) ?: throw InsertException("image.insertfail.record")
                    }
                    return@let dto.url
                }
            } ?: throw ExternalIntegrationException("external.presignedfail.image")

        // todo fix if webp is default
        return PutImageUseCaseOut(url)
    }
}