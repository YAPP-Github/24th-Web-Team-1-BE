package com.few.api.client.repo

import com.few.api.client.config.properties.DiscordBodyProperty
import com.few.api.client.config.properties.Embed
import com.few.api.client.repo.dto.RepoAlterArgs
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RepoClient(
    private val restTemplate: RestTemplate,
    @Value("\${webhook.discord}") private val discordWebhook: String,
) {
    private val log = KotlinLogging.logger {}

    fun announceRepoAlter(args: RepoAlterArgs) {
        val embedsList = ArrayList<Embed>()
        args.let {
            embedsList.add(
                Embed(
                    title = "Exception",
                    description = it.exception.toString()
                )
            )
            it.requestURL.let { requestURL ->
                embedsList.add(
                    Embed(
                        title = "Request URL",
                        description = requestURL
                    )
                )
            }
            it.query?.let { query ->
                embedsList.add(
                    Embed(
                        title = "Slow Query Detected",
                        description = query
                    )
                )
            }
        }
        args.let {
            DiscordBodyProperty(
                content = "😭 DB 이상 발생",
                embeds = embedsList
            )
        }.let { body ->
            restTemplate.exchange(
                discordWebhook,
                HttpMethod.POST,
                HttpEntity(body),
                String::class.java
            ).let { res ->
                log.info { "Discord webhook response: ${res.statusCode}" }
            }
        }
    }
}