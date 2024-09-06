package com.few.api.web.config.converter

import com.few.api.web.support.DayCode
import org.springframework.core.convert.converter.Converter

class DayCodeConverter : Converter<String, DayCode> {
    override fun convert(source: String): DayCode {
        return DayCode.fromCode(source)
    }
}