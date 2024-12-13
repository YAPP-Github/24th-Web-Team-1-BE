package com.few.api.domain.member.exception

import com.few.api.config.ApiMessageSourceAccessor

class NotValidTokenException : IllegalStateException {
    private val code: String

    constructor(errorCode: String) : super(ApiMessageSourceAccessor.getMessage(errorCode)) {
        this.code = errorCode
    }

    constructor(
        errorCode: String,
        vararg args: Any?,
    ) : super(ApiMessageSourceAccessor.getMessage(errorCode, args)) {
        this.code = errorCode
    }

    constructor(code: String, cause: Throwable?) : super(
        ApiMessageSourceAccessor.getMessage(
            code
        ),
        cause
    ) {
        this.code = code
    }

    constructor(
        code: String,
        cause: Throwable?,
        vararg args: Any?,
    ) : super(ApiMessageSourceAccessor.getMessage(code, args), cause) {
        this.code = code
    }
}