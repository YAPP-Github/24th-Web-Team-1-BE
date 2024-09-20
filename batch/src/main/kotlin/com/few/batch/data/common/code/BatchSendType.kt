package com.few.batch.data.common.code

/**
 * @see com.few.data.common.code.SendType
 */
enum class BatchSendType(val code: Byte) {
    EMAIL(0),
    AWSSES(1),
}