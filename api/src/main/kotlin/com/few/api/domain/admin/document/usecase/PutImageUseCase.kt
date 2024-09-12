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
        val originImage = File.createTempFile("temp", ".$suffix").apply {
            imageSource.transferTo(this)
        }

        val webpImage = ImmutableImage.loader().fromFile(originImage)
            .output(WebpWriter.DEFAULT, File.createTempFile("temp", ".webp"))

        val url = putImageService.execute(imageName, originImage)
            ?.`object`
            ?.let { source ->
                getUrlService.execute(GetUrlInDto(source)).also { dto ->
                    imageDao.insertImageIfo(InsertImageIfoCommand(source, dto.url))
                        ?: throw InsertException("image.insertfail.record")
                }
                    .let { savedImage ->
                        savedImage.url
                    }
            } ?: throw ExternalIntegrationException("external.presignedfail.image")

        val webpUrl = putImageService.execute(imageName, webpImage)
            ?.`object`
            ?.let { source ->
                getUrlService.execute(GetUrlInDto(source)).also { dto ->
                    imageDao.insertImageIfo(InsertImageIfoCommand(source, dto.url))
                        ?: throw InsertException("image.insertfail.record")
                }
                    .let { savedImage ->
                        savedImage.url
                    }
            } ?: throw ExternalIntegrationException("external.presignedfail.image")

        return PutImageUseCaseOut(url, listOf(suffix, "webp"))
    }
}