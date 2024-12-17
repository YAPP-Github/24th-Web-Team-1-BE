package storage.document.client

import com.amazonaws.services.s3.AmazonS3Client
import io.github.oshai.kotlinlogging.KotlinLogging
import storage.document.client.dto.DocumentGetPreSignedObjectUrlArgs
import storage.document.client.dto.DocumentPutObjectArgs
import storage.document.client.dto.DocumentWriteResponse
import storage.document.client.dto.toS3Args

class S3DocumentStoreClient(
    private val s3client: AmazonS3Client,
    private val region: String,
) : DocumentStoreClient {
    private val log = KotlinLogging.logger {}

    override fun getPreSignedObjectUrl(args: DocumentGetPreSignedObjectUrlArgs): String? {
        args
            .toS3Args()
            .let { s3 ->
                try {
                    s3client
                        .generatePresignedUrl(s3)
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
        args
            .toS3Args()
            .let { s3 ->
                try {
                    s3client.putObject(s3).let { owr ->
                        return DocumentWriteResponse(
                            s3.bucketName,
                            region,
                            args.imagePath,
                            owr.eTag ?: "",
                            owr.versionId ?: "",
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