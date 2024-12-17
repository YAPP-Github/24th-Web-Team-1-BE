package com.few.api.domain.common.vo

enum class ViewCategory(
    val viewName: String,
) {
    MAIN_CARD("mainCard"),
    MY_PAGE("myPage"),
    ;

    companion object {
        fun fromViewName(viewName: String): ViewCategory? = ViewCategory.entries.find { it.viewName == viewName }
    }
}