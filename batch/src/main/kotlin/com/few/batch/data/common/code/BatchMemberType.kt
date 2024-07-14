package com.few.batch.data.common.code

/**
 * BatchMemberType is origin from MemberType in few-data module.
 * @see com.few.data.common.code.MemberType
 */
enum class BatchMemberType(val code: Byte, val displayName: String) {
    NORMAL(60, "일반멤버"),
    ADMIN(0, "어드민멤버"),
    WRITER(120, "작가멤버"),
    ;

    companion object {
        fun fromCode(code: Byte): BatchMemberType? {
            return values().find { it.code == code }
        }
    }
}