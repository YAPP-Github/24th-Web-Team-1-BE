package com.few.api.domain.common.vo

enum class MemberType(
    val code: Byte,
    val displayName: String,
) {
    PREAUTH(30, "가입대기멤버"),
    NORMAL(60, "일반멤버"),
    ADMIN(0, "어드민멤버"),
    WRITER(120, "작가멤버"),
    ;

    companion object {
        fun fromCode(code: Byte): MemberType? = entries.find { it.code == code }
    }
}