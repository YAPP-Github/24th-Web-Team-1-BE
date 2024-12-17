package com.few.api.domain.common.vo

enum class WorkBookStatus {
    ACTIVE,
    DONE,
    ;

    companion object {
        /**
         * status ture -> ACTIVE, false -> DONE
         */
        fun fromStatus(status: Boolean): WorkBookStatus = if (status) ACTIVE else DONE
    }
}