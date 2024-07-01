package com.few.storage.document.service.s3

import com.few.storage.document.client.DocumentStoreClient
import com.few.storage.document.client.dto.DocumentWriteResponse
import com.few.storage.document.client.util.DocumentArgsGenerator
import com.few.storage.document.service.PutDocumentService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class S3PutDocumentService(
    @Value("\${document.store.bucket-name}") val bucket: String,
    private val documentStoreClient: DocumentStoreClient
) : PutDocumentService {
    override fun execute(name: String, file: File): DocumentWriteResponse? {
        DocumentArgsGenerator.putDocument(bucket, name, file).let { args ->
            return documentStoreClient.putObject(args)
        }
    }
}