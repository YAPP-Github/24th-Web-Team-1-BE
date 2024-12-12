package architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag

@Tag("ArchitectureSpec")
class RepoModuleArchitectureSpec {
    companion object {
        var repoClasses: JavaClasses? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            repoClasses = ClassFileImporter().importPackages("repo")
        }
    }

    @Test
    fun `repo 모듈의 설정 클래스는 config 패키지에 존재합니다`() {
        val rule: ArchRule = classes()
            .that()
            .resideInAPackage("repo")
            .and().haveNameNotMatching(".*Companion*.")
            .and().areAnnotatedWith(Configuration::class.java)
            .should()
            .resideInAPackage("repo.config")

        rule.check(repoClasses)
    }
}