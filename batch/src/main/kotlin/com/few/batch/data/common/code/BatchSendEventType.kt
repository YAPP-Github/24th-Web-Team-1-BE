package com.few.batch.data.common.code

/**
 * @see com.few.data.common.code.SendEventType
 */
enum class BatchSendEventType(val code: Byte, val type: String) {

    OPEN(0, "open"),
    DELIVERY(1, "delivery"),
    CLICK(2, "click"),
    SEND(3, "send"),
    DELIVERYDELAY(4, "deliverydelay"),
    ;

    companion object {
        fun fromType(type: String): BatchSendEventType? {
            return entries.find { it.type == type }
        }

        fun fromCode(code: Byte): BatchSendEventType? {
            return entries.find { it.code == code }
        }
    }
}