package repo.event

data class SlowQueryEvent(
    val slowQuery: String,
)