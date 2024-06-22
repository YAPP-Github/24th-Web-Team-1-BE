package com.few.api.web.controller.workbook.request

data class CancelSubWorkBookBody(
    val email: String,
    val opinion: String,
    val reason: String
)