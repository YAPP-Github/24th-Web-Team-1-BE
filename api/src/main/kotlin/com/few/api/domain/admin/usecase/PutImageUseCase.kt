package com.few.api.domain.admin.usecase

import com.few.api.domain.admin.repo.image.ImageDao
import com.few.api.domain.admin.repo.image.command.InsertImageIfoCommand
import com.few.api.domain.admin.service.GetUrlService
import com.few.api.domain.admin.service.dto.GetUrlInDto
import com.few.api.domain.admin.usecase.dto.PutImageUseCaseIn
import com.few.api.domain.admin.usecase.dto.PutImageUseCaseOut
import com.few.api.domain.admin.utils.ObjectPathGenerator
import com.few.api.domain.common.exception.ExternalIntegrationException
import com.few.api.domain.common.exception.InsertException
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional
import storage.image.PutImageProvider
import java.io.File

@Component
class PutImageUseCase(
    private val imageDao: ImageDao,
    private val putImageService: PutImageProvider,
    private val getUrlService: GetUrlService,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: PutImageUseCaseIn): PutImageUseCaseOut {
        val imageSource = useCaseIn.source
        val suffix = imageSource.originalFilename?.substringAfterLast(".") ?: "jpg"

        val imageName = ObjectPathGenerator.imagePath(suffix)
        val originImage =
            File.createTempFile("temp", ".$suffix").apply {
                imageSource.transferTo(this)
            }

        val webpImage =
            ImmutableImage
                .loader()
                .fromFile(originImage)
                .output(WebpWriter.DEFAULT, File.createTempFile("temp", ".webp"))

        val url =
            putImageService
                .execute(imageName, originImage)
                ?.`object`
                ?.let { source ->
                    getUrlService
                        .execute(GetUrlInDto(source))
                        .also { dto ->
                            imageDao.insertImageIfo(InsertImageIfoCommand(source, dto.url))
                                ?: throw InsertException("image.insertfail.record")
                        }.let { savedImage ->
                            savedImage.url
                        }
                } ?: throw ExternalIntegrationException("external.presignedfail.image")

        val webpUrl =
            putImageService
                .execute(imageName, webpImage)
                ?.`object`
                ?.let { source ->
                    getUrlService
                        .execute(GetUrlInDto(source))
                        .also { dto ->
                            imageDao.insertImageIfo(InsertImageIfoCommand(source, dto.url))
                                ?: throw InsertException("image.insertfail.record")
                        }.let { savedImage ->
                            savedImage.url
                        }
                } ?: throw ExternalIntegrationException("external.presignedfail.image")

        return PutImageUseCaseOut(url, listOf(suffix, "webp"))
    }
}