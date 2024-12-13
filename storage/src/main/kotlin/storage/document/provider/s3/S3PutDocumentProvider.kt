package storage.document.provider.s3

import storage.document.client.DocumentStoreClient
import storage.document.client.dto.DocumentWriteResponse
import storage.document.client.util.DocumentArgsGenerator
import storage.document.PutDocumentProvider
import java.io.File

class S3PutDocumentProvider(
    val bucket: String,
    private val documentStoreClient: DocumentStoreClient,
) : PutDocumentProvider {
    override fun execute(name: String, file: File): DocumentWriteResponse? {
        DocumentArgsGenerator.putDocument(bucket, name, file).let { args ->
            return documentStoreClient.putObject(args)
        }
    }
}