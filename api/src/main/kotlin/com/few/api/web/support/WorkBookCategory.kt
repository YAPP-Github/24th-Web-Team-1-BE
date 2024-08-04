package com.few.api.web.support

/**
 * WorkBookCategory is origin from CategoryType in few-data module.
 * @see com.few.data.common.code.CategoryType
 */
enum class WorkBookCategory(val code: Byte, val parameterName: String, val displayName: String) {
    All(-1, "all", "전체"),
    ECONOMY(0, "economy", "경제"),
    IT(10, "it", "IT"),
    MARKETING(20, "marketing", "마케팅"),
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