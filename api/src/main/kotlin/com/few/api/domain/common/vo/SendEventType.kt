package com.few.api.domain.common.vo

enum class SendEventType(val code: Byte, val type: String) {
    OPEN(0, "open"),
    DELIVERY(1, "delivery"),
    CLICK(2, "click"),
    SEND(3, "send"),
    DELIVERYDELAY(4, "deliverydelay"),
    ;

    companion object {
        fun fromType(type: String): SendEventType? {
            return entries.find { it.type == type }
        }

        fun fromCode(code: Byte): SendEventType? {
            return entries.find { it.code == code }
        }
    }
}