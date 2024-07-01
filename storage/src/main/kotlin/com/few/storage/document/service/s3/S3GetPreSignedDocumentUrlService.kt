package com.few.storage.document.service.s3

import com.few.storage.document.client.DocumentStoreClient
import com.few.storage.document.client.util.DocumentArgsGenerator
import com.few.storage.document.service.GetPreSignedDocumentUrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3GetPreSignedDocumentUrlService(
    @Value("\${document.store.bucket-name}") val bucket: String,
    private val documentStoreClient: DocumentStoreClient
) : GetPreSignedDocumentUrlService {
    override fun execute(image: String): String? {
        DocumentArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return documentStoreClient.getPreSignedObjectUrl(args)
        }
    }
}