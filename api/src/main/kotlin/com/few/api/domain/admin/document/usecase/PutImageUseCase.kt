package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.dto.PutImageUseCaseIn
import com.few.api.domain.admin.document.dto.PutImageUseCaseOut
import com.few.api.domain.admin.document.service.GetUrlService
import com.few.api.domain.admin.document.service.dto.GetUrlQuery
import com.few.api.domain.admin.document.utils.ObjectPathGenerator
import com.few.api.repo.dao.image.ImageDao
import com.few.api.repo.dao.image.command.InsertImageIfoCommand
import com.few.storage.image.service.PutImageService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File

@Component
class PutImageUseCase(
    private val imageDao: ImageDao,
    private val putImageService: PutImageService,
    private val getUrlService: GetUrlService
) {

    @Transactional
    fun execute(useCaseIn: PutImageUseCaseIn): PutImageUseCaseOut {
        val imageSource = useCaseIn.source
        val suffix = imageSource.originalFilename?.substringAfterLast(".") ?: "jpg"

        val image = runCatching {
            File.createTempFile("temp", ".$suffix")
        }.onSuccess {
            imageSource.transferTo(it)
        }.getOrThrow()
        val imageName = ObjectPathGenerator.imagePath(suffix)

        val url = putImageService.execute(imageName, image)?.let { res ->
            val source = res.`object`
            GetUrlQuery(source).let { query ->
                getUrlService.execute(query)
            }.let { url ->
                InsertImageIfoCommand(source, url).let { command ->
                    imageDao.insertImageIfo(command) ?: throw IllegalStateException("Failed to save image info")
                }
                return@let url
            }
        }

        return PutImageUseCaseOut(url!!)
    }
}