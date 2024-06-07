import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version DependencyVersion.KOTLIN
    kotlin("plugin.spring") version DependencyVersion.KOTLIN
    kotlin("plugin.allopen") version DependencyVersion.KOTLIN
    kotlin("kapt") version DependencyVersion.KOTLIN

    /** spring */
    id("org.springframework.boot") version DependencyVersion.SPRING_BOOT
    id("io.spring.dependency-management") version DependencyVersion.SPRING_DEPENDENCY_MANAGEMENT

    /** ktlint */
    id("org.jlleitschuh.gradle.ktlint") version DependencyVersion.KTLINT

    /** docs */
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
    group = "com.few"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    tasks.withType<Wrapper> {
        gradleVersion = "8.5"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.getByName("bootJar") {
    enabled = false
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.hidetake.swagger.generator")

    /**
     * https://kotlinlang.org/docs/reference/compiler-plugins.html#spring-support
     * automatically supported annotation
     * @Component, @Async, @Transactional, @Cacheable, @SpringBootTest,
     * @Configuration, @Controller, @RestController, @Service, @Repository.
     * jpa meta-annotations not automatically opened through the default settings of the plugin.spring
     */
    allOpen {
    }

    dependencies {
        /** spring starter */
        implementation("org.springframework.boot:spring-boot-starter-validation")
        kapt("org.springframework.boot:spring-boot-configuration-processor")

        /** kotlin */
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        /** logger */
        implementation("net.logstash.logback:logstash-logback-encoder:${DependencyVersion.LOGBACK_ENCODER}")

        /** test **/
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.mockk:mockk:${DependencyVersion.MOCKK}")

        /** kotest */
        testImplementation("io.kotest:kotest-runner-junit5:${DependencyVersion.KOTEST}")
        testImplementation("io.kotest:kotest-assertions-core:${DependencyVersion.KOTEST}")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:${DependencyVersion.KOTEST_EXTENSION}")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersion.COROUTINE_TEST}")

        /** swagger */
        swaggerUI("org.webjars:swagger-ui:${DependencyVersion.SWAGGER_UI}")
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = true
    }

    defaultTasks("bootRun")
}
