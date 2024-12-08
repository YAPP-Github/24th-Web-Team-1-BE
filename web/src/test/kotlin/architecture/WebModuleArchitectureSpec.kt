package architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration

@Tag("ArchitectureSpec")
class WebModuleArchitectureSpec {
    companion object {
        var webClasses: JavaClasses? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            webClasses = ClassFileImporter().importPackages("web", "security")
        }
    }

    @Test
    fun `web 모듈의 설정 클래스는 config 패키지에 존재합니다`() {
        val rule = noClasses()
            .that()
            .resideInAPackage("web")
            .and().haveNameNotMatching(".*Companion*.")
            .and().resideOutsideOfPackages("web.security")
            .and().areAnnotatedWith(Configuration::class.java)
            .should()
            .resideInAPackage("web.config")

        rule.check(webClasses)
    }

    @Test
    fun `web 모듈안의 security 패키지는 하나의 모듈처럼 격리되어야 합니다`() {
        val rule = noClasses()
            .that()
            .resideInAnyPackage("web")
            .and().haveNameNotMatching(".*Companion*.")
            .and()
            .resideOutsideOfPackages("web.security")
            .and().haveNameNotMatching(".*Companion*.")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("security")

        rule.check(webClasses)
    }

    @Test
    fun `web 모듈안의 client 패키지는 하나의 모듈처럼 격리되어야 합니다`() {
        val rule = noClasses()
            .that()
            .resideInAPackage("web")
            .and().haveNameNotMatching(".*Companion*.")
            .and().resideOutsideOfPackages("web.client")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("web.client")

        rule.check(webClasses)
    }
}