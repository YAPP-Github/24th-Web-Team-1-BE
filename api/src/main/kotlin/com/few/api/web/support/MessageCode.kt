package com.few.api.web.support

/** 메시지 코드  */
enum class MessageCode(val code: String, val value: String) {
    /** 성공 메시지 코드  */
    SUCCESS("success", "성공"),

    /** 삭제 메시지 코드  */
    RESOURCE_DELETED("resource.deleted", "삭제되었습니다."),

    /** 수정 메시지 코드  */
    RESOURCE_UPDATED("resource.updated", "수정되었습니다."),

    /** 생성 메시지 코드  */
    RESOURCE_CREATED("resource.created", "새로 생성되었습니다.")
}