package com.few.api.web.support

enum class WorkBookStatus {
    ACTIVE,
    DONE,
    ;

    companion object {
        /**
         * status ture -> ACTIVE, false -> DONE
         */
        fun fromStatus(status: Boolean): WorkBookStatus {
            return if (status) ACTIVE else DONE
        }
    }
}