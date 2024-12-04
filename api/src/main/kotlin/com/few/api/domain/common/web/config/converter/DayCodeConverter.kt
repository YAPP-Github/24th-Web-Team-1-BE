package com.few.api.domain.common.web.config.converter

import com.few.api.domain.common.vo.DayCode
import org.springframework.core.convert.converter.Converter

class DayCodeConverter : Converter<String, DayCode> {
    override fun convert(source: String): DayCode {
        return DayCode.fromCode(source)
    }
}