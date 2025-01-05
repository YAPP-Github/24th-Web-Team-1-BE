package com.few.crm.support.jpa.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter : AttributeConverter<List<String>, String> {
    companion object {
        const val SPLIT_CHAR: String = ", "
    }

    override fun convertToDatabaseColumn(stringList: List<String>?): String? = stringList?.joinToString(SPLIT_CHAR)

    override fun convertToEntityAttribute(string: String): List<String> = string.split(SPLIT_CHAR)
}