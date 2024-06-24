package com.few.data.common.code

enum class MemberType(val code: Byte, val displayName: String) {
    NORMAL(60, "일반멤버"),
    ADMIN(0, "어드민멤버"),
    WRITER(120, "작가멤버");

    companion object {
        fun fromCode(code: Byte): MemberType? {
            return values().find { it.code == code }
        }
    }
}