package com.few.storage.document.service

import com.few.storage.PutObjectService
import com.few.storage.document.client.dto.DocumentWriteResponse
import java.io.File

interface PutDocumentService : PutObjectService<DocumentWriteResponse> {
    override fun execute(name: String, file: File): DocumentWriteResponse?
}