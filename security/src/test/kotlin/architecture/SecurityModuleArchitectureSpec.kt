package architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration

@Tag("ArchitectureSpec")
class SecurityModuleArchitectureSpec {
    companion object {
        var securityClasses: JavaClasses? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            securityClasses = ClassFileImporter().importPackages("security")
        }
    }

    @Test
    fun `security 모듈의 설정 클래스는 config 패키지에 존재합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("security")
            .and().haveNameNotMatching(".*Companion*.")
            .and().areAnnotatedWith(Configuration::class.java)
            .should()
            .resideInAPackage("security.config")

        rule.check(securityClasses)
    }
}