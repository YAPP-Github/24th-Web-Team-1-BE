package com.few.api.web.config.converter

import com.few.api.web.support.SendType
import org.springframework.core.convert.converter.Converter

class SendTypeConverter : Converter<String, SendType> {
    override fun convert(source: String): SendType {
        return SendType.fromCode(source.toByte())
    }
}