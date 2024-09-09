package com.few.api.domain.common.lock

enum class LockIdentifier {
    /**
     * 구독 테이블에 멤버와 워크북을 기준으로 락을 건다.
     */
    SUBSCRIPTION_MEMBER_ID_WORKBOOK_ID,
}