package storage.document.config

import com.amazonaws.services.s3.AmazonS3Client
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent
import storage.config.StorageClientConfig
import storage.document.GetPreSignedDocumentUrlProvider
import storage.document.PutDocumentProvider
import storage.document.client.DocumentStoreClient
import storage.document.client.S3DocumentStoreClient
import storage.document.provider.s3.S3GetPreSignedDocumentUrlProvider
import storage.document.provider.s3.S3PutDocumentProvider

@Configuration
@Import(StorageClientConfig::class)
class S3DocumentStoreConfig(
    @Value("\${document.store.bucket-name}") val bucket: String,
    @Value("\${storage.region}") val region: String,
) : ApplicationListener<ContextRefreshedEvent> {
    companion object {
        const val S3_DOCUMENT_STORE_CLIENT = DocumentStorageConfig.BEAN_NAME_PREFIX + "S3DocumentStoreClient"
        const val S3_PUT_DOCUMENT_PROVIDER = DocumentStorageConfig.BEAN_NAME_PREFIX + "S3PutDocumentProvider"
        const val S3_GET_PRE_SIGNED_DOCUMENT_URL_PROVIDER = DocumentStorageConfig.BEAN_NAME_PREFIX + "S3GetPreSignedDocumentUrlProvider"
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

    @Bean(name = [S3_PUT_DOCUMENT_PROVIDER])
    fun s3PutDocumentProvider(
        @Value("\${document.store.bucket-name}") bucket: String,
        documentStoreClient: DocumentStoreClient,
    ): PutDocumentProvider = S3PutDocumentProvider(bucket, documentStoreClient)

    @Bean(name = [S3_GET_PRE_SIGNED_DOCUMENT_URL_PROVIDER])
    fun s3GetPreSignedDocumentUrlProvider(
        @Value("\${document.store.bucket-name}") bucket: String,
        documentStoreClient: DocumentStoreClient,
    ): GetPreSignedDocumentUrlProvider = S3GetPreSignedDocumentUrlProvider(bucket, documentStoreClient)
}