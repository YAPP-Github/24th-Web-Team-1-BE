package com.few.api.web.handler

enum class ExceptionMessage(val code: String, val message: String) {
    FAIL("fail", "알 수 없는 오류가 발생했어요."),
    FAIL_NOT_FOUND("fail.notfound", "일치하는 결과를 찾을 수 없어요."),
    FAIL_AUTHENTICATION("fail.authentication", "인증이 필요해요."),
    FAIL_REQUEST("fail.request", "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND("resource.notfound", "요청과 일치하는 결과를 찾을 수 없어요."),
    RESOURCE_DELETED("resource.deleted", "요청에 대한 응답을 찾을 수 없어요."),
    ACCESS_DENIED("access.denied", "접근 권한이 없어요."),
    REQUEST_INVALID_FORMAT("request.%s.invalid", "잘못된 요청입니다."),
    REQUEST_INVALID("request.invalid", "잘못된 요청입니다.")
}