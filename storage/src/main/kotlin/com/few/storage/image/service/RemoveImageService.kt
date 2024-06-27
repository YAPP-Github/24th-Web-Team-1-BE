package com.few.image.service

fun interface RemoveImageService {
    fun execute(image: String): Boolean
}