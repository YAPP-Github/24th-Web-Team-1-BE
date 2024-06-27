package com.few.api.domain.image.usecase

import com.few.api.domain.image.dto.PutImageUseCaseIn
import com.few.api.domain.image.dto.PutImageUseCaseOut
import com.few.api.domain.image.service.GetUrlService
import com.few.api.domain.image.service.dto.GetUrlQuery
import com.few.api.repo.dao.image.ImageDao
import com.few.api.repo.dao.image.command.InsertImageIfoCommand
import com.few.image.service.PutImageService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.time.LocalDate
import kotlin.random.Random

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
        val dateDir = LocalDate.now().toString()
        val imageName = "$dateDir/${generateImageName()}" + ".$suffix"

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

    private fun generateImageName(): String {
        return randomString()
    }

    private fun randomString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..16)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }
}