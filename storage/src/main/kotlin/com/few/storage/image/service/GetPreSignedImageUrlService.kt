package com.few.storage.image.service

fun interface GetPreSignedImageUrlService {
    fun execute(image: String): String?
}