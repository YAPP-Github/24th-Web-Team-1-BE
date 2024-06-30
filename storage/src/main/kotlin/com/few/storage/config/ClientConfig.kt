package com.few.storage.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientConfig(
    @Value("\${storage.url}") val url: String,
    @Value("\${storage.access-key}") val accessKey: String,
    @Value("\${storage.secret-key}") val secretKey: String,
    @Value("\${storage.region}") val region: String
) {

    @Bean
    fun s3StorageClient(): AmazonS3Client {
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
            return client as AmazonS3Client
        }
    }
}