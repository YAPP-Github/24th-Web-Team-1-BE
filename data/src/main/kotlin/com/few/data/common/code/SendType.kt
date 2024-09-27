package com.few.data.common.code

/**
 * @see com.few.api.web.support.SendType
 * @see com.few.batch.data.common.code.BatchSendType
 */
enum class SendType(val code: Byte) {
    EMAIL(0),
    AWSSES(1),
}