package com.few.api.domain

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ArchitectureSpec")
class ApiDomainArchitectureSpec {
    companion object {
        var apiDomainClasses: JavaClasses? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            apiDomainClasses = ClassFileImporter().importPackages(
                "com.few.api.domain.article",
                "com.few.api.domain.log",
                "com.few.api.domain.member",
                "com.few.api.domain.problem",
                "com.few.api.domain.subscription",
                "com.few.api.domain.workbook"
            )
        }
    }

    @Test
    fun `Controller 레이어는 다른 레이어에서 접근할 수 없어야 한다`() {
        val rule = layeredArchitecture()
            .layer("controller").definedBy("..controller..")
            .whereLayer("controller").mayNotBeAccessedByAnyLayer()

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Usecase 레이어는 Controller 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("usecase").definedBy("..usecase..")
            .layer("controller").definedBy("..controller..")
            .whereLayer("usecase").mayOnlyBeAccessedByLayers("controller")

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Service 레이어는 Usecase와 Event 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("service").definedBy("..service..")
            .layer("usecase").definedBy("..usecase..")
            .layer("event").definedBy("..event..")
            .whereLayer("service").mayOnlyBeAccessedByLayers("usecase", "event")

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Repo 레이어는 Usecase, Service, Event 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("repo").definedBy("..repo..")
            .layer("usecase").definedBy("..usecase..")
            .layer("service").definedBy("..service..")
            .layer("event").definedBy("..event..")
            .whereLayer("repo").mayOnlyBeAccessedByLayers("usecase", "service", "event")

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Event 레이어는 Usecase 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("event").definedBy("..event..")
            .layer("usecase").definedBy("..usecase..")
            .whereLayer("event").mayOnlyBeAccessedByLayers("usecase")

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Email 레이어는 UseCase, Service 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("email").definedBy("..email..")
            .layer("usecase").definedBy("..usecase..")
            .layer("service").definedBy("..service..")
            .whereLayer("email").mayOnlyBeAccessedByLayers("usecase", "service")

        rule.check(apiDomainClasses)
    }

    @Test
    fun `Client 레이어는 UseCase, Event 레이어에서만 접근 가능하다`() {
        val rule = layeredArchitecture()
            .layer("client").definedBy("..client..")
            .layer("usecase").definedBy("..usecase..")
            .layer("event").definedBy("..event..")
            .whereLayer("client").mayOnlyBeAccessedByLayers("usecase", "event")

        rule.check(apiDomainClasses)
    }
}