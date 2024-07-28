package com.few.api.web.config.converter

import com.few.api.web.support.WorkBookCategory
import org.springframework.core.convert.converter.Converter

class WorkBookCategoryConverter : Converter<String, WorkBookCategory> {

    override fun convert(source: String): WorkBookCategory? {
        return WorkBookCategory.fromCode(source.toByte())
    }
}