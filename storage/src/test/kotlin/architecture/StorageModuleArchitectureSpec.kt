package architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration
import storage.GetPreSignedObjectUrlProvider
import storage.PutObjectProvider
import storage.RemoveObjectProvider

@Tag("ArchitectureSpec")
class StorageModuleArchitectureSpec {
    companion object {
        var storageClasses: JavaClasses? = null

        @BeforeAll
        @JvmStatic
        fun setup() {
            storageClasses = ClassFileImporter().importPackages("storage")
        }
    }

    @Test
    fun `storage 모듈의 설정 클래스는 config 패키지에 존재합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("storage")
            .and().haveNameNotMatching(".*Companion*.")
            .and().resideOutsideOfPackages("storage.document", "storage.image")
            .and().areAnnotatedWith(Configuration::class.java)
            .should()
            .resideInAPackage("storage.config")

        rule.check(storageClasses)
    }

    @Test
    fun `storage 모듈의 object의 url을 제공하기 위한 provider는 GetPreSignedObjectUrlProvider를 구현해야합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("storage.document")
            .or().resideInAPackage("storage.image")
            .and().haveSimpleNameStartingWith("Get")
            .and().haveSimpleNameEndingWith("Provider")
            .and().haveNameNotMatching(".*Companion*.")
            .and().areNotInterfaces()
            .should()
            .implement(
                DescribedPredicate.describe(
                    "GetPreSignedObjectUrlProvider를 구현해야합니다"
                ) { clazz ->
                    clazz.interfaces.javaClass.interfaces.contains(GetPreSignedObjectUrlProvider::class.java)
                }
            )

        rule.check(storageClasses)
    }

    @Test
    fun `storage 모듈의 object를 수정하기 위한 provider는 PutObjectProvider를 구현해야합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("storage.document")
            .or().resideInAPackage("storage.image")
            .and().haveSimpleNameStartingWith("Put")
            .and().haveSimpleNameEndingWith("Provider")
            .and().haveNameNotMatching(".*Companion*.")
            .and().areNotInterfaces()
            .should()
            .implement(
                DescribedPredicate.describe(
                    "PutObjectProvider를 구현해야합니다"
                ) { clazz ->
                    clazz.interfaces.javaClass.interfaces.contains(PutObjectProvider::class.java)
                }
            )

        rule.check(storageClasses)
    }

    @Test
    fun `storage 모듈의 object를 삭제하기 위한 provider는 RemoveObjectProvider를 구현해야합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("storage.document")
            .or().resideInAPackage("storage.image")
            .and().haveSimpleNameStartingWith("Remove")
            .and().haveSimpleNameEndingWith("Provider")
            .and().haveNameNotMatching(".*Companion*.")
            .and().areNotInterfaces()
            .should()
            .implement(
                DescribedPredicate.describe(
                    "RemoveObjectProvider를 구현해야합니다"
                ) { clazz ->
                    clazz.interfaces.javaClass.interfaces.contains(RemoveObjectProvider::class.java)
                }
            )

        rule.check(storageClasses)
    }

    @Test
    fun `client 패키지의  client 클래스는 provider 패키지의 provider 클래스에서만 사용되어야 합니다`() {
        val rule = classes()
            .that()
            .resideInAPackage("storage.*.client")
            .and().haveSimpleNameEndingWith("Client")
            .and().haveNameNotMatching(".*Companion*.")
            .should()
            .onlyBeAccessed().byAnyPackage(
                "storage.*.client",
                "storage.*.provider.*",
                "storage.*.config"
            )

        rule.check(storageClasses)
    }

    @Nested
    inner class DocumentArchitectureSpec {
        @Test
        fun `document 패키지의 설정 클래스는 config 패키지에 존재합니다`() {
            val rule = classes()
                .that()
                .resideInAPackage("storage.document")
                .and().haveNameNotMatching(".*Companion*.")
                .and().areAnnotatedWith(Configuration::class.java)
                .should()
                .resideInAPackage("storage.document.config")

            rule.check(storageClasses)
        }
    }

    @Nested
    inner class ImageArchitectureSpec {
        @Test
        fun `image 패키지의 설정 클래스는 config 패키지에 존재합니다`() {
            val rule = classes()
                .that()
                .resideInAPackage("storage.image")
                .and().haveNameNotMatching(".*Companion*.")
                .and().areAnnotatedWith(Configuration::class.java)
                .should()
                .resideInAPackage("storage.image.config")

            rule.check(storageClasses)
        }
    }
}