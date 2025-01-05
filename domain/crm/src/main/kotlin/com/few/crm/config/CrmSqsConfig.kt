package com.few.crm.config

import com.few.crm.config.CrmConfig.Companion.BEAN_NAME_PREFIX
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient

@Configuration
class CrmSqsConfig {
    companion object {
        const val SQS_ASYNC_CLIENT = BEAN_NAME_PREFIX + "SqsAsyncClient"
        const val SQS_TEMPLATE = BEAN_NAME_PREFIX + "SqsTemplate"
        const val SQS_LISTENER_CONTAINER_FACTORY = BEAN_NAME_PREFIX + "defaultSqsListenerContainerFactory"
    }

    @Value("\${crm.credentials.access-key}")
    val accessKey: String? = null

    @Value("\${crm.credentials.secret-key}")
    val secretKey: String? = null

    @Value("\${crm.region.static}")
    val region: String? = null

    private fun credentialProvider(): AwsCredentials =
        object : AwsCredentials {
            override fun accessKeyId(): String = accessKey ?: ""

            override fun secretAccessKey(): String = secretKey ?: ""
        }

    @Bean(SQS_ASYNC_CLIENT)
    fun sqsAsyncClient(): SqsAsyncClient =
        SqsAsyncClient
            .builder()
            .credentialsProvider(this::credentialProvider)
            .region(Region.of(region))
            .build()

    @Bean(SQS_LISTENER_CONTAINER_FACTORY)
    fun defaultSqsListenerContainerFactory(): SqsMessageListenerContainerFactory<Any> =
        SqsMessageListenerContainerFactory
            .builder<Any>()
            .configure { opt ->
                opt.acknowledgementMode(AcknowledgementMode.MANUAL)
            }.sqsAsyncClient(sqsAsyncClient())
            .build()

    @Bean(SQS_TEMPLATE)
    fun sqsTemplate(): SqsTemplate = SqsTemplate.newTemplate(sqsAsyncClient())
}