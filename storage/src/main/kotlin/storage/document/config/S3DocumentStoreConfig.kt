package storage.document.config

import com.amazonaws.services.s3.AmazonS3Client
import storage.config.StorageClientConfig
import storage.document.client.DocumentStoreClient
import storage.document.client.S3DocumentStoreClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent

@Configuration
@Import(StorageClientConfig::class)
class S3DocumentStoreConfig(
    @Value("\${document.store.bucket-name}") val bucket: String,
    @Value("\${storage.region}") val region: String,
) : ApplicationListener<ContextRefreshedEvent> {
    companion object {
        const val S3_DOCUMENT_STORE_CLIENT = DocumentStorageConfig.BEAN_NAME_PREFIX + "S3DocumentStoreClient"
    }

    private val log = KotlinLogging.logger {}

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

    @Bean(name = [S3_DOCUMENT_STORE_CLIENT])
    fun s3DocumentStoreClient(s3StorageClient: AmazonS3Client): DocumentStoreClient {
        client = s3StorageClient
        return S3DocumentStoreClient(client!!, region)
    }
}