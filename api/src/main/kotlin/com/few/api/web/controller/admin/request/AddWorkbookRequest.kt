package com.few.api.web.controller.admin.request

import jakarta.validation.constraints.NotBlank
import java.net.URL

data class AddWorkbookRequest(
    @field:NotBlank(message = "{workbook.title.notblank}")
    val title: String,
    @field:NotBlank(message = "{image.url.notblank}")
    val mainImageUrl: URL,
    @field:NotBlank(message = "{category.notblank}")
    val category: String,
    @field:NotBlank(message = "{workbook.description.notblank}")
    val description: String
)