package com.few.crm.email.domain

enum class EmailSendEventType(
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
        fun fromType(type: String): EmailSendEventType? = entries.find { it.type.equals(type, ignoreCase = true) }

        fun fromCode(code: Byte): EmailSendEventType? = entries.find { it.code == code }
    }
}