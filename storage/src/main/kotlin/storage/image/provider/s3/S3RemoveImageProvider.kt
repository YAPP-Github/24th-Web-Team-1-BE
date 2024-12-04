package storage.image.provider.s3

import storage.image.client.ImageStoreClient
import storage.image.client.util.ImageArgsGenerator
import storage.image.RemoveImageProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3RemoveImageProvider(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : RemoveImageProvider {
    override fun execute(image: String): Boolean {
        ImageArgsGenerator.remove(bucket, image).let { args ->
            return imageStoreClient.removeObject(args)
        }
    }
}