package com.few.api.web.config.converter

import com.few.api.web.support.ViewCategory
import org.springframework.core.convert.converter.Converter

class ViewConverter : Converter<String, ViewCategory> {

    override fun convert(source: String): ViewCategory? {
        return ViewCategory.fromViewName(source)
    }
}