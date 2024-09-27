package com.few.batch.data.common.code

/**
 * BatchCategoryType is origin from CategoryType in few-data module.
 * @see com.few.data.common.code.CategoryType
 */
enum class BatchCategoryType(val code: Byte, val displayName: String) {
    ECONOMY(0, "경제"),
    IT(10, "IT"),
    MARKETING(20, "마케팅"),
    LANGUAGE(25, "외국어"),
    CULTURE(30, "교양"),
    SCIENCE(40, "과학"),
    ;

    companion object {
        fun fromCode(code: Byte): BatchCategoryType? {
            return entries.find { it.code == code }
        }

        fun convertToCode(displayName: String): Byte {
            return entries.find { it.name == displayName }?.code ?: throw IllegalArgumentException("Invalid category name $displayName")
        }

        fun convertToDisplayName(code: Byte): String {
            return entries.find { it.code == code }?.displayName ?: throw IllegalArgumentException("Invalid category code $code")
        }
    }
}