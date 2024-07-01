package com.few.storage.document.client

import com.few.storage.document.client.dto.DocumentGetPreSignedObjectUrlArgs
import com.few.storage.document.client.dto.DocumentPutObjectArgs
import com.few.storage.document.client.dto.DocumentWriteResponse

interface DocumentStoreClient {
    fun getPreSignedObjectUrl(args: DocumentGetPreSignedObjectUrlArgs): String?

    fun putObject(args: DocumentPutObjectArgs): DocumentWriteResponse?
}