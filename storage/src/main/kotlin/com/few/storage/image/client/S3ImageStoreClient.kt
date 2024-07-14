package com.few.storage.image.client

import com.amazonaws.services.s3.AmazonS3Client
import com.few.storage.image.client.dto.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class S3ImageStoreClient(
    private val s3client: AmazonS3Client,
    private val region: String,
) : ImageStoreClient {

    val log: Logger = LoggerFactory.getLogger(S3ImageStoreClient::class.java)

    override fun getPreSignedObjectUrl(args: ImageGetPreSignedObjectUrlArgs): String? {
        args.toS3Args()
            .let { s3 ->
                try {
                    s3client.generatePresignedUrl(s3)
                        .let { url ->
                            return url.toString()
                        }
                } catch (e: Exception) {
                    log.debug("Failed to get presigned url for object: ${args.imagePath}")
                    log.warn(e.message)
                    log.warn(e.stackTraceToString())
                    return null
                }
            }
    }

    override fun removeObject(args: ImageRemoveObjectArgs): Boolean {
        args.toS3Args()
            .let { s3 ->
                try {
                    s3client.deleteObject(s3)
                    return true
                } catch (e: Exception) {
                    log.debug("Failed to remove object: ${args.imagePath}")
                    log.warn(e.message)
                    log.warn(e.stackTraceToString())
                    return false
                }
            }
    }

    override fun putObject(args: ImagePutObjectArgs): ImageWriteResponse? {
        args.toS3Args()
            .let { s3 ->
                try {
                    s3client.putObject(s3).let { owr ->
                        return ImageWriteResponse(
                            s3.bucketName,
                            region,
                            args.imagePath,
                            owr.eTag ?: "",
                            owr.versionId ?: ""
                        )
                    }
                } catch (e: Exception) {
                    log.debug("Failed to put object: ${args.imagePath}")
                    log.warn(e.message)
                    log.warn(e.stackTraceToString())
                    return null
                }
            }
    }
}