package com.few.storage.document.config

import com.amazonaws.services.s3.AmazonS3Client
import com.few.storage.config.ClientConfig
import com.few.storage.document.client.DocumentStoreClient
import com.few.storage.document.client.S3DocumentStoreClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent

@Configuration
@Import(ClientConfig::class)
class S3DocumentStoreConfig(
    @Value("\${document.store.bucket-name}") val bucket: String,
    @Value("\${storage.region}") val region: String,
) : ApplicationListener<ContextRefreshedEvent> {
    var log: Logger = LoggerFactory.getLogger(S3DocumentStoreConfig::class.java)

    private var client: AmazonS3Client? = null

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        client?.let { client ->
            client.listBuckets().let { buckets ->
                if (buckets.none { it.name == bucket }) {
                    client.createBucket(bucket)
                    log.info("Create bucket $bucket")
                }
                log.info("Bucket $bucket already exists")
            }
        }
    }

    @Bean
    fun s3DocumentStoreClient(s3StorageClient: AmazonS3Client): DocumentStoreClient {
        client = s3StorageClient
        return S3DocumentStoreClient(client!!, region)
    }
}