package web.client

data class DiscordBodyProperty(
    val content: String,
    val embeds: List<Embed>,
)

data class Embed(
    val title: String,
    val description: String,
)