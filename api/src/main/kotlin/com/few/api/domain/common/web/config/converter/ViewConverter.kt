package com.few.api.domain.common.web.config.converter

import com.few.api.domain.common.vo.ViewCategory
import org.springframework.core.convert.converter.Converter

class ViewConverter : Converter<String, ViewCategory> {

    override fun convert(source: String): ViewCategory? {
        return ViewCategory.fromViewName(source)
    }
}