package storage.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class StorageClientConfig(
    @Value("\${storage.url}") val url: String,
    @Value("\${storage.access-key}") val accessKey: String,
    @Value("\${storage.secret-key}") val secretKey: String,
    @Value("\${storage.region}") val region: String,
) {
    @Profile("!prd")
    @Bean
    fun localS3StorageClient(): AmazonS3Client {
        val builder =
            AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(
                            accessKey,
                            secretKey,
                        ),
                    ),
                ).withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        url,
                        region,
                    ),
                )

        builder.build().let { client ->
            return client as AmazonS3Client
        }
    }

    @Profile("prd")
    @Bean
    fun prdS3StorageClient(): AmazonS3Client {
        AmazonS3Client
            .builder()
            .withRegion(region)
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        accessKey,
                        secretKey,
                    ),
                ),
            ).build()
            .let { client ->
                return client as AmazonS3Client
            }
    }
}