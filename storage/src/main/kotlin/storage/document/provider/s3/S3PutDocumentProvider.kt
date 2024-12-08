package storage.document.provider.s3

import storage.document.client.DocumentStoreClient
import storage.document.client.dto.DocumentWriteResponse
import storage.document.client.util.DocumentArgsGenerator
import storage.document.PutDocumentProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class S3PutDocumentProvider(
    @Value("\${document.store.bucket-name}") val bucket: String,
    private val documentStoreClient: DocumentStoreClient,
) : PutDocumentProvider {
    override fun execute(name: String, file: File): DocumentWriteResponse? {
        DocumentArgsGenerator.putDocument(bucket, name, file).let { args ->
            return documentStoreClient.putObject(args)
        }
    }
}