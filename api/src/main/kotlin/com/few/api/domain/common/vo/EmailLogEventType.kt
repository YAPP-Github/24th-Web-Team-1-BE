package com.few.api.domain.common.vo

enum class EmailLogEventType(
    val code: Byte,
    val type: String,
) {
    OPEN(0, "open"),
    DELIVERY(1, "delivery"),
    CLICK(2, "click"),
    SEND(3, "send"),
    DELIVERYDELAY(4, "deliverydelay"),
    ;

    companion object {
        fun fromType(type: String): EmailLogEventType? = entries.find { it.type == type.lowercase() }

        fun fromCode(code: Byte): EmailLogEventType? = entries.find { it.code == code }
    }
}