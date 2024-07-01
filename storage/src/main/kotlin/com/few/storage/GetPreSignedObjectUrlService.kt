package com.few.storage

interface GetPreSignedObjectUrlService {

    fun execute(image: String): String?
}