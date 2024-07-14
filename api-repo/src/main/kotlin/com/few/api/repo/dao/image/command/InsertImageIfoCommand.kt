package com.few.api.repo.dao.image.command

import java.net.URL

data class InsertImageIfoCommand(
    val imagePath: String,
    val url: URL,
    val alias: String = "",
)