package com.few.storage.document.service

import com.few.storage.GetPreSignedObjectUrlService

interface GetPreSignedDocumentUrlService : GetPreSignedObjectUrlService {
    override fun execute(image: String): String?
}