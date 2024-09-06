package com.few.api.web.support

enum class ViewCategory(val viewName: String) {
    MAIN_CARD("mainCard"),
    MY_PAGE("myPage"),
    ;

    companion object {
        fun fromViewName(viewName: String): ViewCategory? {
            return ViewCategory.entries.find { it.viewName == viewName }
        }
    }
}