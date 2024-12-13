package storage.document.client.util

import storage.document.client.dto.DocumentGetPreSignedObjectUrlArgs
import storage.document.client.dto.DocumentPutObjectArgs
import storage.document.client.dto.DocumentRemoveObjectArgs
import org.apache.http.entity.ContentType
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class DocumentArgsGenerator {
    companion object {
        fun preSignedUrl(bucket: String, document: String): DocumentGetPreSignedObjectUrlArgs {
            return DocumentGetPreSignedObjectUrlArgs(bucket, document, "GET")
        }

        fun putDocument(bucket: String, name: String, document: File): DocumentPutObjectArgs {
            return DocumentPutObjectArgs(bucket, name, BufferedInputStream(FileInputStream(document)), document.length(), -1, ContentType.APPLICATION_OCTET_STREAM)
        }

        fun remove(bucket: String, document: String): DocumentRemoveObjectArgs {
            return DocumentRemoveObjectArgs(bucket, document)
        }
    }
}