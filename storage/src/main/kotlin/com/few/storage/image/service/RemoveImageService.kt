package com.few.storage.image.service

fun interface RemoveImageService {
    fun execute(image: String): Boolean
}