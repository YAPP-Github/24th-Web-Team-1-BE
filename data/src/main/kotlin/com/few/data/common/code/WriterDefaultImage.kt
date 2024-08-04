package com.few.data.common.code

enum class WriterDefaultImage(val url: String) { // TODO: 작가 저장 시 사용
    DEFAULT_IMG1("https://github.com/user-attachments/assets/28df9078-488c-49d6-9375-54ce5a250742"),
    DEFAULT_IMG2("https://github.com/user-attachments/assets/385dcafd-6737-41d7-aaa0-9db4ea6f27ea"),
    DEFAULT_IMG3("https://github.com/user-attachments/assets/209da8ff-7c78-41b7-8e3e-40a2705f714a"),

    ;

    companion object {
        fun getRandom(): WriterDefaultImage {
            return entries.toTypedArray().random()
        }
    }
}

const val MEMBER_DEFAULT_IMG_URL = "https://github.com/user-attachments/assets/c33361bd-978e-41c0-85e8-7ab5c9005687"