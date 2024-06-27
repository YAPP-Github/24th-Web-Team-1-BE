package com.few.image.service

fun interface GetPreSignedImageUrlService {
    fun execute(image: String): String?
}