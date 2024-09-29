package com.few.api.web.support

/**
 * @see com.few.data.common.code.SendType
 */
enum class SendType(val code: Byte) {
    EMAIL(0),
    AWSSES(1),
    ;

    companion object {
        fun fromCode(code: Byte): SendType {
            return entries.first { it.code == code }
        }
    }
}