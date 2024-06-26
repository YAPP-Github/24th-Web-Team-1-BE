import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version DependencyVersion.KOTLIN
    kotlin("plugin.spring") version DependencyVersion.KOTLIN
    kotlin("plugin.allopen") version DependencyVersion.KOTLIN
    kotlin("kapt") version DependencyVersion.KOTLIN

    /** spring */
    id("org.springframework.boot") version DependencyVersion.SPRING_BOOT
    id("io.spring.dependency-management") version DependencyVersion.SPRING_DEPENDENCY_MANAGEMENT

    /** jooq */
    id("org.jooq.jooq-codegen-gradle") version DependencyVersion.JOOQ

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

    sourceSets {
        main {
            java {
                val mainDir = "src/main/kotlin"
                val jooqDir = "src/generated"
                srcDirs(mainDir, jooqDir)
            }
        }
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
    apply(plugin = "org.jooq.jooq-codegen-gradle")

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

        /** jooq */
        implementation("org.springframework.boot:spring-boot-starter-jooq")
        implementation("org.jooq:jooq:${DependencyVersion.JOOQ}")
        implementation("org.jooq:jooq-meta:${DependencyVersion.JOOQ}")
        implementation("org.jooq:jooq-codegen:${DependencyVersion.JOOQ}")
        jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

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

    /** copy data migration */
    tasks.create("copyDataMigration") {
        doLast {
            val root = rootDir
            val flyWayResourceDir = "/db/migration/entity"
            val dataMigrationDir = "$root/data/$flyWayResourceDir"
            File(dataMigrationDir).walkTopDown().forEach {
                if (it.isFile) {
                    it.copyTo(
                        File("${project.projectDir}/src/main/resources$flyWayResourceDir/${it.name}"),
                        true
                    )
                }
            }
        }
    }

    /** copy data migration before compile kotlin */
    tasks.getByName("compileKotlin") {
        dependsOn("copyDataMigration")
    }

    /** jooq codegen after copy data migration */
    tasks.getByName("jooqCodegen") {
        dependsOn("copyDataMigration")
    }

    jooq {
        configuration {
            generator {
                database {
                    name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                    properties {
                        // Specify the location of your SQL script.
                        // You may use ant-style file matching, e.g. /path/**/to/*.sql
                        //
                        // Where:
                        // - ** matches any directory subtree
                        // - * matches any number of characters in a directory / file name
                        // - ? matches a single character in a directory / file name
                        property {
                            key = "scripts"
                            value = "src/main/resources/db/migration/**/*.sql"
                        }

                        // The sort order of the scripts within a directory, where:
                        //
                        // - semantic: sorts versions, e.g. v-3.10.0 is after v-3.9.0 (default)
                        // - alphanumeric: sorts strings, e.g. v-3.10.0 is before v-3.9.0
                        // - flyway: sorts files the same way as flyway does
                        // - none: doesn't sort directory contents after fetching them from the directory
                        property {
                            key = "sort"
                            value = "flyway"
                        }

                        // The default schema for unqualified objects:
                        //
                        // - public: all unqualified objects are located in the PUBLIC (upper case) schema
                        // - none: all unqualified objects are located in the default schema (default)
                        //
                        // This configuration can be overridden with the schema mapping feature
                        property {
                            key = "unqualifiedSchema"
                            value = "none"
                        }

                        // The default name case for unquoted objects:
                        //
                        // - as_is: unquoted object names are kept unquoted
                        // - upper: unquoted object names are turned into upper case (most databases)
                        // - lower: unquoted object names are turned into lower case (e.g. PostgreSQL)
                        property {
                            key = "defaultNameCase"
                            value = "as_is"
                        }
                    }
                }

                generate {
                    isDeprecated = false
                    isRecords = true
                    isImmutablePojos = true
                    isFluentSetters = true
                    isJavaTimeTypes = true
                }

                target {
                    packageName = "jooq.jooq_dsl"
                    directory = "src/generated"
                    encoding = "UTF-8"
                }
            }
        }
    }

    defaultTasks("bootRun")
}

/** do all jooq codegen */
tasks.register("jooqCodegenAll") {
    dependsOn(":api:jooqCodegen")
    dependsOn(":api-repo:jooqCodegen")
    dependsOn(":batch:jooqCodegen")
}

/** git hooks */
tasks.register("gitExecutableHooks") {
    doLast {
        Runtime.getRuntime().exec("chmod -R +x .git/hooks/").waitFor()
    }
}

tasks.register<Copy>("installGitHooks") {
    val scriptDir = "${rootProject.rootDir}/scripts"
    from("$scriptDir/pre-commit")
    into("${rootProject.rootDir}/.git/hooks")
}

tasks.named("gitExecutableHooks").configure {
    dependsOn("installGitHooks")
}

tasks.named("clean").configure {
    dependsOn("gitExecutableHooks")
}