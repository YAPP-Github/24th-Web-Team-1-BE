package storage.image.provider.s3

import storage.image.client.ImageStoreClient
import storage.image.client.dto.ImageWriteResponse
import storage.image.client.util.ImageArgsGenerator
import storage.image.PutImageProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class S3PutImageProvider(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : PutImageProvider {
    override fun execute(name: String, file: File): ImageWriteResponse? {
        ImageArgsGenerator.putImage(bucket, name, file).let { args ->
            return imageStoreClient.putObject(args)
        }
    }
}