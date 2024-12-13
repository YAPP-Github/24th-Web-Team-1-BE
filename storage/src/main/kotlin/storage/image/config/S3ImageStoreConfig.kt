package storage.image.config

import com.amazonaws.services.s3.AmazonS3Client
import storage.image.client.ImageStoreClient
import storage.image.client.S3ImageStoreClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import storage.image.GetPreSignedImageUrlProvider
import storage.image.PutImageProvider
import storage.image.RemoveImageProvider
import storage.image.provider.s3.S3GetPreSignedImageUrlProvider
import storage.image.provider.s3.S3PutImageProvider
import storage.image.provider.s3.S3RemoveImageProvider

@Configuration
class S3ImageStoreConfig(
    @Value("\${image.store.bucket-name}") val bucket: String,
    @Value("\${storage.region}") val region: String,
) : ApplicationListener<ContextRefreshedEvent> {
    private val log = KotlinLogging.logger {}
    companion object {
        const val S3_IMAGE_STORE_CLIENT = ImageStorageConfig.BEAN_NAME_PREFIX + "S3ImageStoreClient"
        const val S3_PUT_IMAGE_PROVIDER = ImageStorageConfig.BEAN_NAME_PREFIX + "S3PutImageProvider"
        const val S3_GET_PRE_SIGNED_IMAGE_URL_PROVIDER = ImageStorageConfig.BEAN_NAME_PREFIX + "S3GetPreSignedImageUrlProvider"
        const val S3_REMOVE_IMAGE_PROVIDER = ImageStorageConfig.BEAN_NAME_PREFIX + "S3RemoveImageProvider"
    }

    private var client: AmazonS3Client? = null

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        client?.let { client ->
            client.listBuckets().let { buckets ->
                if (buckets.none { it.name == bucket }) {
                    client.createBucket(bucket)
                    log.info { "Create bucket $bucket" }
                }
                log.info { "Bucket $bucket already exists" }
            }
        }
    }

    @Bean(name = [S3_IMAGE_STORE_CLIENT])
    fun s3ImageStoreClient(s3StorageClient: AmazonS3Client): ImageStoreClient {
        client = s3StorageClient
        return S3ImageStoreClient(client!!, region)
    }

    @Bean(name = [S3_PUT_IMAGE_PROVIDER])
    fun s3PutImageProvider(
        @Value("\${image.store.bucket-name}") bucket: String,
        imageStoreClient: ImageStoreClient,
    ): PutImageProvider {
        return S3PutImageProvider(bucket, imageStoreClient)
    }

    @Bean(name = [S3_GET_PRE_SIGNED_IMAGE_URL_PROVIDER])
    fun s3GetPreSignedImageUrlProvider(
        @Value("\${image.store.bucket-name}") bucket: String,
        imageStoreClient: ImageStoreClient,
    ): GetPreSignedImageUrlProvider {
        return S3GetPreSignedImageUrlProvider(bucket, imageStoreClient)
    }

    @Bean(name = [S3_REMOVE_IMAGE_PROVIDER])
    fun s3DeleteImageProvider(
        @Value("\${image.store.bucket-name}") bucket: String,
        imageStoreClient: ImageStoreClient,
    ): RemoveImageProvider {
        return S3RemoveImageProvider(bucket, imageStoreClient)
    }
}