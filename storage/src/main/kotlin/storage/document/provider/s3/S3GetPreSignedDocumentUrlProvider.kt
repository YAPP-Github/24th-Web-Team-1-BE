package storage.document.provider.s3

import storage.document.client.DocumentStoreClient
import storage.document.client.util.DocumentArgsGenerator
import storage.document.GetPreSignedDocumentUrlProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3GetPreSignedDocumentUrlProvider(
    @Value("\${document.store.bucket-name}") val bucket: String,
    private val documentStoreClient: DocumentStoreClient,
) : GetPreSignedDocumentUrlProvider {
    override fun execute(image: String): String? {
        DocumentArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return documentStoreClient.getPreSignedObjectUrl(args)
        }
    }
}