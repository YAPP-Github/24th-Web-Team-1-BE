package com.few.api.domain.subscription.client

import com.few.api.domain.subscription.client.dto.WorkbookSubscriptionArgs
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import web.client.DiscordBodyProperty
import web.client.Embed

@Service
class ApiSubscriptionClient(
    private val restTemplate: RestTemplate,
    @Value("\${webhook.discord}") private val discordWebhook: String,
) {
    private val log = KotlinLogging.logger {}

    fun announceWorkbookSubscription(args: WorkbookSubscriptionArgs) {
        val body =
            args.let { arg ->
                DiscordBodyProperty(
                    content = "🎉 신규 구독 알림 ",
                    embeds =
                        listOf(
                            Embed(
                                title = "Total Subscriptions",
                                description = arg.totalSubscriptions.toString(),
                            ),
                            Embed(
                                title = "Active Subscriptions",
                                description = arg.activeSubscriptions.toString(),
                            ),
                            Embed(
                                title = "Workbook Title",
                                description = arg.workbookTitle,
                            ),
                        ),
                )
            }

        restTemplate
            .exchange(
                discordWebhook,
                HttpMethod.POST,
                HttpEntity(body),
                String::class.java,
            ).let { res ->
                log.info { "Discord webhook response: ${res.statusCode}" }
            }
    }
}