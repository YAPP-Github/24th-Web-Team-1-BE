package com.few.storage.document.client

import com.amazonaws.services.s3.AmazonS3Client
import com.few.storage.document.client.dto.DocumentGetPreSignedObjectUrlArgs
import com.few.storage.document.client.dto.DocumentPutObjectArgs
import com.few.storage.document.client.dto.DocumentWriteResponse
import com.few.storage.document.client.dto.toS3Args
import io.github.oshai.kotlinlogging.KotlinLogging

class S3DocumentStoreClient(
    private val s3client: AmazonS3Client,
    private val region: String
) : DocumentStoreClient {

    private val log = KotlinLogging.logger {}

    override fun getPreSignedObjectUrl(args: DocumentGetPreSignedObjectUrlArgs): String? {
        args.toS3Args()
            .let { s3 ->
                try {
                    s3client.generatePresignedUrl(s3)
                        .let { url ->
                            return url.toString()
                        }
                } catch (e: Exception) {
                    log.debug { "Failed to get presigned url for object: ${args.imagePath}" }
                    log.warn { e.message }
                    log.warn { e.stackTraceToString() }
                    return null
                }
            }
    }

    override fun putObject(args: DocumentPutObjectArgs): DocumentWriteResponse? {
        args.toS3Args()
            .let { s3 ->
                try {
                    s3client.putObject(s3).let { owr ->
                        return DocumentWriteResponse(
                            s3.bucketName,
                            region,
                            args.imagePath,
                            owr.eTag ?: "",
                            owr.versionId ?: ""
                        )
                    }
                } catch (e: Exception) {
                    log.debug { "Failed to put object: ${args.imagePath}" }
                    log.warn { e.message }
                    log.warn { e.stackTraceToString() }
                    return null
                }
            }
    }
}