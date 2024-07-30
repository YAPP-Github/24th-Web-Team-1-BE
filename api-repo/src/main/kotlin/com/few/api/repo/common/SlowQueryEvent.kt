package com.few.api.repo.common

data class SlowQueryEvent(
    val slowQuery: String,
)