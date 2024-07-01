package com.few.api.web.controller.admin.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class MapArticleRequest(
    @field:NotBlank(message = "{min.id}")
    val workbookId: Long,
    @field:NotBlank(message = "{min.id}")
    val articleId: Long,
    @field:Min(value = 1, message = "{min.day}")
    val dayCol: Int
)