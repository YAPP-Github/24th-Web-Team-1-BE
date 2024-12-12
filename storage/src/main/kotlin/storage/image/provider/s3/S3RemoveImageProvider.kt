package storage.image.provider.s3

import storage.image.client.ImageStoreClient
import storage.image.client.util.ImageArgsGenerator
import storage.image.RemoveImageProvider

class S3RemoveImageProvider(
    val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : RemoveImageProvider {
    override fun execute(image: String): Boolean {
        ImageArgsGenerator.remove(bucket, image).let { args ->
            return imageStoreClient.removeObject(args)
        }
    }
}