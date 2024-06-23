package com.few.data.common.code

enum class MemberType(val code: Byte, val displayName: String) {
    NORMAL_USER(60, "일반유저"),
    ADMIN_USER(0, "어드민유저"),
    AUTHOR_USER(120, "작가유저");

    companion object {
        fun fromCode(code: Byte): MemberType? {
            return values().find { it.code == code }
        }
    }
}