package storage.document.provider.s3

import storage.document.client.DocumentStoreClient
import storage.document.client.util.DocumentArgsGenerator
import storage.document.GetPreSignedDocumentUrlProvider

class S3GetPreSignedDocumentUrlProvider(
    val bucket: String,
    private val documentStoreClient: DocumentStoreClient,
) : GetPreSignedDocumentUrlProvider {
    override fun execute(image: String): String? {
        DocumentArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return documentStoreClient.getPreSignedObjectUrl(args)
        }
    }
}