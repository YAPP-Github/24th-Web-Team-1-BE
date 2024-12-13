package com.few.api.domain.admin.service.dto

import java.util.*

/** query.object example: images/2024-07-01/14789db.png */
fun GetUrlInDto.getPreSignedUrlServiceKey(): String {
    return this.`object`.split("/")[0].lowercase(Locale.getDefault()).replace("s", "")
}
data class GetUrlInDto(
    val `object`: String,
)