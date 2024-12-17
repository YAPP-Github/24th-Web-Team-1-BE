package storage.image.provider.s3

import storage.image.GetPreSignedImageUrlProvider
import storage.image.client.ImageStoreClient
import storage.image.client.util.ImageArgsGenerator

class S3GetPreSignedImageUrlProvider(
    val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : GetPreSignedImageUrlProvider {
    override fun execute(image: String): String? {
        ImageArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return imageStoreClient.getPreSignedObjectUrl(args)
        }
    }
}