package com.few.api.exception.member

import com.few.api.exception.support.MessageSourceAccessor

class NotValidTokenException : IllegalStateException {
    private val code: String

    constructor(errorCode: String) : super(MessageSourceAccessor.getMessage(errorCode)) {
        this.code = errorCode
    }

    constructor(
        errorCode: String,
        vararg args: Any?,
    ) : super(MessageSourceAccessor.getMessage(errorCode, args)) {
        this.code = errorCode
    }

    constructor(code: String, cause: Throwable?) : super(
        MessageSourceAccessor.getMessage(
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
    ) : super(MessageSourceAccessor.getMessage(code, args), cause) {
        this.code = code
    }
}