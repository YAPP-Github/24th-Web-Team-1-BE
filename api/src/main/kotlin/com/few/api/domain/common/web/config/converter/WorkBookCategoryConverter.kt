package com.few.api.domain.common.web.config.converter

import com.few.api.domain.common.vo.WorkBookCategory
import org.springframework.core.convert.converter.Converter

class WorkBookCategoryConverter : Converter<String, WorkBookCategory> {

    override fun convert(source: String): WorkBookCategory? {
        return WorkBookCategory.fromCode(source.toByte())
    }
}