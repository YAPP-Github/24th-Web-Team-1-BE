package com.few.api.web.config.converter

import com.few.api.web.support.SendType
import org.springframework.core.convert.converter.Converter

class SendTypeConverter : Converter<Byte, SendType> {
    override fun convert(source: Byte): SendType {
        return SendType.fromCode(source)
    }
}