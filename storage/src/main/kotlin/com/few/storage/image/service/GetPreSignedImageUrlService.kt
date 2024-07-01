package com.few.storage.image.service

import com.few.storage.GetPreSignedObjectUrlService

fun interface GetPreSignedImageUrlService : GetPreSignedObjectUrlService {
    override fun execute(image: String): String?
}