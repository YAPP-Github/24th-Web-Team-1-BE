package com.few.api.client.subscription

import com.few.api.client.config.properties.DiscordBodyProperty
import com.few.api.client.config.properties.Embed
import com.few.api.client.subscription.dto.WorkbookSubscriptionArgs
import org.apache.juli.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SubscriptionClient(
    private val restTemplate: RestTemplate,
    @Value("\${webhook.discord}") private val discordWebhook: String
) {
    private val log = LogFactory.getLog(SubscriptionClient::class.java)

    fun announceWorkbookSubscription(args: WorkbookSubscriptionArgs) {
        args.let {
            DiscordBodyProperty(
                content = "🎉 신규 구독 알림 ",
                embeds = listOf(
                    Embed(
                        title = "Total Subscriptions",
                        description = it.totalSubscriptions.toString()
                    ),
                    Embed(
                        title = "Active Subscriptions",
                        description = it.activeSubscriptions.toString()
                    ),
                    Embed(
                        title = "Workbook Title",
                        description = it.workbookTitle
                    )
                )
            )
        }.let { body ->
            restTemplate.exchange(
                discordWebhook,
                HttpMethod.POST,
                HttpEntity(body),
                String::class.java
            ).let { res ->
                log.info("Discord webhook response: ${res.statusCode}")
            }
        }
    }
}