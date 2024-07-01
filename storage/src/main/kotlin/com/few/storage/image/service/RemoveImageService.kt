package com.few.storage.image.service

import com.few.storage.RemoveObjectService

fun interface RemoveImageService : RemoveObjectService {
    override fun execute(image: String): Boolean
}