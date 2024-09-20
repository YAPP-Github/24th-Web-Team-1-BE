package com.few.api.web.config.converter

import com.few.api.web.support.EmailLogEventType
import org.springframework.core.convert.converter.Converter

class EmailLogEventTypeConverter : Converter<String, EmailLogEventType> {
    override fun convert(source: String): EmailLogEventType {
        return EmailLogEventType.fromType(source)
            ?: throw IllegalArgumentException("EmailLogEventType not found. type=$source")
    }
}