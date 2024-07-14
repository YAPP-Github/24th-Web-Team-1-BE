package com.few.storage.image.config

import com.amazonaws.services.s3.AmazonS3Client
import com.few.storage.image.client.ImageStoreClient
import com.few.storage.image.client.S3ImageStoreClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent

@Configuration
class S3ImageStoreConfig(
    @Value("\${image.store.bucket-name}") val bucket: String,
    @Value("\${storage.region}") val region: String
) : ApplicationListener<ContextRefreshedEvent> {

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

    @Bean
    fun s3ImageStoreClient(s3StorageClient: AmazonS3Client): ImageStoreClient {
        client = s3StorageClient
        return S3ImageStoreClient(client!!, region)
    }
}