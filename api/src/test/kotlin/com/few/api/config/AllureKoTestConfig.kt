package com.few.api.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.allure.AllureTestReporter

class AllureKoTestConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(AllureTestReporter())
}