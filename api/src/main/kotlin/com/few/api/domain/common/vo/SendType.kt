package com.few.api.domain.common.vo

enum class SendType(
    val code: Byte,
) {
    EMAIL(0),
    AWSSES(1),
    ;

    companion object {
        fun fromCode(code: Byte): SendType = entries.first { it.code == code }
    }
}