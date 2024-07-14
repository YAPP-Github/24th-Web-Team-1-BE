package com.few.storage.image.service.support

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class CdnProperty(
    @Value("\${cdn.url}") val url: String,
)