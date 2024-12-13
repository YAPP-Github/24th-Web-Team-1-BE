package storage.document.client

import storage.document.client.dto.DocumentGetPreSignedObjectUrlArgs
import storage.document.client.dto.DocumentPutObjectArgs
import storage.document.client.dto.DocumentWriteResponse

interface DocumentStoreClient {
    fun getPreSignedObjectUrl(args: DocumentGetPreSignedObjectUrlArgs): String?

    fun putObject(args: DocumentPutObjectArgs): DocumentWriteResponse?
}