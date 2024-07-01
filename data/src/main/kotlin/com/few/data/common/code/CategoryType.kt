package com.few.data.common.code

enum class CategoryType(val code: Byte, val displayName: String) {
    POLITICS(0, "정치"),
    ECONOMY(10, "경제"),
    SOCIETY(20, "사회"),
    CULTURE(30, "문화"),
    LIFE(40, "생활"),
    IT(50, "IT"),
    SCIENCE(60, "과학"),
    ENTERTAINMENTS(70, "엔터테인먼트"),
    SPORTS(80, "스포츠"),
    GLOBAL(90, "국제"),
    ETC(100, "기타");

    companion object {
        fun fromCode(code: Byte): CategoryType? {
            return entries.find { it.code == code }
        }

        fun convertToCode(displayName: String): Byte {
            return entries.find { it.name == displayName }?.code ?: throw IllegalArgumentException("Invalid category name")
        }

        fun convertToDisplayName(code: Byte): String {
            return entries.find { it.code == code }?.displayName ?: throw IllegalArgumentException("Invalid category code")
        }
    }
}