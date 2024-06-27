package com.few.storage.image.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.few.storage.image.client.ImageStoreClient
import com.few.storage.image.client.S3ImageStoreClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent

@Configuration
class S3ImageStoreConfig(
    @Value("\${image.store.url}") val url: String,
    @Value("\${image.store.access-key}") val accessKey: String,
    @Value("\${image.store.secret-key}") val secretKey: String,
    @Value("\${image.store.bucket-name}") val bucket: String,
    @Value("\${image.store.region}") val region: String
) : ApplicationListener<ContextRefreshedEvent> {

    var log: Logger = LoggerFactory.getLogger(S3ImageStoreConfig::class.java)

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
    fun s3ImageStoreClient(): ImageStoreClient {
        val builder = AmazonS3ClientBuilder.standard()
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        accessKey,
                        secretKey
                    )
                )
            )
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    url,
                    region
                )
            )

        builder.build().let { client ->
            this.client = client as AmazonS3Client
            return S3ImageStoreClient(client, region)
        }
    }
}