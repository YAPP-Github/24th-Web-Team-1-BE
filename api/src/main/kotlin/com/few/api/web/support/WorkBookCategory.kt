package com.few.api.web.support

/**
 * BatchCategoryType is origin from CategoryType in few-data module.
 * @see com.few.data.common.code.CategoryType
 */
enum class WorkBookCategory(val code: Byte, val parameterName: String) {
    All(-1, "all"),
    ECONOMY(0, "economy"),
    IT(10, "it"),
    MARKETING(20, "marketing"),
    CULTURE(30, "culture"),
    SCIENCE(40, "science"),
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