package storage.document

import storage.PutObjectProvider
import storage.document.client.dto.DocumentWriteResponse
import java.io.File

interface PutDocumentProvider : PutObjectProvider<DocumentWriteResponse> {
    override fun execute(
        name: String,
        file: File,
    ): DocumentWriteResponse?
}