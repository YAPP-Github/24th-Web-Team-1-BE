package com.few.api.web.support

/**
 * @see com.few.data.common.code.SendEventType
 */
enum class EmailLogEventType(val code: Byte, val type: String) {
    OPEN(0, "open"),
    DELIVERY(1, "delivery"),
    CLICK(2, "click"),
    SEND(3, "send"),
    DELIVERYDELAY(4, "deliverydelay"),
    ;

    companion object {
        fun fromType(type: String): EmailLogEventType? {
            return entries.find { it.type == type.lowercase() }
        }

        fun fromCode(code: Byte): EmailLogEventType? {
            return entries.find { it.code == code }
        }
    }
}