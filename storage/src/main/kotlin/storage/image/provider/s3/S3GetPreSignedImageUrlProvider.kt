package storage.image.provider.s3

import storage.image.client.ImageStoreClient
import storage.image.client.util.ImageArgsGenerator
import storage.image.GetPreSignedImageUrlProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class S3GetPreSignedImageUrlProvider(
    @Value("\${image.store.bucket-name}") val bucket: String,
    private val imageStoreClient: ImageStoreClient,
) : GetPreSignedImageUrlProvider {
    override fun execute(image: String): String? {
        ImageArgsGenerator.preSignedUrl(bucket, image).let { args ->
            return imageStoreClient.getPreSignedObjectUrl(args)
        }
    }
}