package com.few.api.domain.common.vo

enum class WorkBookCategory(val code: Byte, val parameterName: String, val displayName: String) {
    All(-1, "all", "전체"),
    ECONOMY(0, "economy", "경제"),
    IT(10, "it", "IT"),
    MARKETING(20, "marketing", "마케팅"),
    LANGUAGE(25, "language", "외국어"),
    CULTURE(30, "culture", "교양"),
    SCIENCE(40, "science", "과학"),
    ;

    companion object {
        fun fromCode(code: Byte): WorkBookCategory? {
            return entries.find { it.code == code }
        }

        fun convertToCode(parameterName: String): Byte {
            return entries.find { it.parameterName == parameterName }?.code ?: throw IllegalArgumentException("Invalid category name $parameterName")
        }
    }
}