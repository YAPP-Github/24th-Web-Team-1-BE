import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version DependencyVersion.KOTLIN
    kotlin("plugin.spring") version DependencyVersion.KOTLIN
    kotlin("plugin.allopen") version DependencyVersion.KOTLIN
    kotlin("kapt") version DependencyVersion.KOTLIN

    /** spring */
    id("org.springframework.boot") version DependencyVersion.SPRING_BOOT
    id("io.spring.dependency-management") version DependencyVersion.SPRING_DEPENDENCY_MANAGEMENT

    id("java-test-fixtures")

    /** jooq */
    id("org.jooq.jooq-codegen-gradle") version DependencyVersion.JOOQ

    /** docs */
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR

    /** sentry */
    id("io.sentry.jvm.gradle") version DependencyVersion.SENTRY_JVM_GRADLE

    id("org.jetbrains.dokka") version "1.9.20"
}

java.sourceCompatibility = JavaVersion.VERSION_18

allprojects {
    group = "com.few"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    repositories {
        mavenCentral()
    }

    val ktlint by configurations.creating

    dependencies {
        ktlint("com.pinterest.ktlint:ktlint-cli:${DependencyVersion.PINTEREST_KTLINT}") {
            attributes {
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            }
        }
    }

    val ktlintCheck by tasks.registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Check Kotlin code style"
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args(
            "**/src/**/*.kt",
            "**.kts",
            "!**/build/**",
        )
    }

    tasks.check {
        dependsOn(ktlintCheck)
    }

    tasks.register<JavaExec>("ktlintFormat") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Check Kotlin code style and format"
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        args(
            "-F",
            "**/src/**/*.kt",
            "**.kts",
            "!**/build/**",
        )
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
    apply(plugin = "org.hidetake.swagger.generator")
    apply(plugin = "org.jooq.jooq-codegen-gradle")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "org.asciidoctor.jvm.convert")
    apply(plugin = "com.epages.restdocs-api-spec")
    apply(plugin = "org.hidetake.swagger.generator")
    apply(plugin = "io.sentry.jvm.gradle")

    /**
     * https://kotlinlang.org/docs/reference/compiler-plugins.html#spring-support
     * automatically supported annotation
     * @Component, @Async, @Transactional, @Cacheable, @SpringBootTest,
     * @Configuration, @Controller, @RestController, @Service, @Repository.
     * jpa meta-annotations not automatically opened through the default settings of the plugin.spring
     */
    allOpen {
    }

    dependencyManagement {
        dependencies {
            /**
             * spring boot starter jooq 3.2.5 default jooq version is 3.18.14.
             * But jooq-codegen-gradle need over 3.19.0.
             *  */
            dependency("org.jooq:jooq:${DependencyVersion.JOOQ}")
            imports {
                mavenBom("org.springframework.modulith:spring-modulith-bom:${DependencyVersion.SPRING_MODULITH}")
            }
        }
    }

    dependencies {
        /** spring starter */
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.modulith:spring-modulith-starter-core")
        kapt("org.springframework.boot:spring-boot-configuration-processor")

        /** kotlin */
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

        /** test **/
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.mockk:mockk:${DependencyVersion.MOCKK}")
        testImplementation("com.tngtech.archunit:archunit-junit5:${DependencyVersion.ARCH_UNIT_JUNIT5}")
        testImplementation("org.springframework.modulith:spring-modulith-starter-test")

        /** kotest */
        testImplementation("io.kotest:kotest-runner-junit5:${DependencyVersion.KOTEST}")
        testImplementation("io.kotest:kotest-assertions-core:${DependencyVersion.KOTEST}")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:${DependencyVersion.KOTEST_EXTENSION}")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersion.COROUTINE_TEST}")

        /** Kotlin Logger **/
        implementation("io.github.oshai:kotlin-logging-jvm:${DependencyVersion.KOTLIN_LOGGING}")

        /** apache common */
        implementation("org.apache.commons:commons-lang3:${DependencyVersion.COMMONS_LANG3}")

        /** swagger ui */
        swaggerUI("org.webjars:swagger-ui:${DependencyVersion.SWAGGER_UI}")

        /** sentry */
        implementation("io.sentry:sentry-spring-boot-starter-jakarta:${DependencyVersion.SENTRY_SPRING_BOOT}")
    }

    kapt {
        includeCompileClasspath = false
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        register<Test>("architectureSpecTest") {
            group = "spec"
            useJUnitPlatform {
                includeTags("ArchitectureSpec")
            }
        }
    }

    sentry {
        // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
        // This enables source context, allowing you to see your source
        // code as part of your stack traces in Sentry.
        includeSourceContext = true

        // The organization slug in Sentry.
        org =
            project.hasProperty("sentryProjectName").let {
                if (it) {
                    project.property("sentryProjectName") as String
                } else {
                    ""
                }
            }
        projectName =
            project.hasProperty("sentryProjectName").let {
                if (it) {
                    project.property("sentryProjectName") as String
                } else {
                    ""
                }
            }
        authToken =
            project.hasProperty("sentryAuthToken").let {
                if (it) {
                    project.property("sentryAuthToken") as String
                } else {
                    ""
                }
            }
    }

    /** server url */
    val serverUrl =
        project.hasProperty("serverUrl").let {
            if (it) {
                project.property("serverUrl") as String
            } else {
                "http://localhost:8080"
            }
        }

    /** convert snippet to swagger */
    openapi3 {
        this.setServer(serverUrl)
        title = project.name
        version = project.version.toString()
        format = "yaml"
        snippetsDirectory = "build/generated-snippets/"
        outputDirectory = "src/main/resources/static"
        outputFileNamePrefix = "openapi3"
    }

    /** convert snippet to postman */
    postman {
        title = project.name
        version = project.version.toString()
        baseUrl = serverUrl
        outputDirectory = "src/main/resources/static"
        outputFileNamePrefix = "postman"
    }

    /** generate swagger ui */
    swaggerSources {
        register(project.name) {
            setInputFile(file("$projectDir/src/main/resources/static/openapi3.yaml"))
        }
    }

    /**
     * generate static swagger ui <br/>
     * need snippet to generate swagger ui
     * */
    tasks.register("generateStaticSwaggerUI", Copy::class) {
        val name = project.name
        val generateSwaggerUITask = "generateSwaggerUI${name.first().uppercase() + name.substring(1)}"
        dependsOn(generateSwaggerUITask)

        val generateSwaggerUISampleTask = tasks.named(generateSwaggerUITask, GenerateSwaggerUI::class).get()
        from(generateSwaggerUISampleTask.outputDir)
        into("$projectDir/src/main/resources/static/docs/${project.name}/swagger-ui")
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
                        true,
                    )
                }
            }
        }
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

/** do all copy data migration */
tasks.register("copyDataMigrationAll") {
    dependsOn(":api:copyDataMigration")
}

/** do all jooq codegen */
tasks.register("jooqCodegenAll") {
    dependsOn("copyDataMigrationAll")
    dependsOn(":api:jooqCodegen")
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