import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI

sourceSets {
    main {
        java {
            val mainDir = "src/main/kotlin"
            val jooqDir = "src/generated"
            srcDirs(mainDir, jooqDir)
        }
    }
}

dependencies {
    /** module */
    implementation(project(":api-repo"))
    implementation(project(":batch"))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    /** swagger & restdocs */
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${DependencyVersion.SPRINGDOC}")
    implementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    implementation("com.epages:restdocs-api-spec-mockmvc:${DependencyVersion.EPAGES_REST_DOCS_API_SPEC}")

    /** jooq */
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-meta:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-codegen:${DependencyVersion.JOOQ}")
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")
}

plugins {
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR

    /** jooq */
    id("org.jooq.jooq-codegen-gradle") version DependencyVersion.JOOQ
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

/** convert snippet to swagger */
openapi3 {
    this.setServer("http://localhost:8080") // todo refactor to use setServers
    title = project.name
    version = project.version.toString()
    format = "yaml"
    snippetsDirectory = "build/generated-snippets/"
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "openapi3"
}

/** convert snippet to postman */
postman {
    title = project.name
    version = project.version.toString()
    baseUrl = "http://localhost:8080"
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "postman"
}

swaggerSources {
    register("api") {
        setInputFile(file("$projectDir/src/main/resources/static/openapi3.yaml"))
    }
}

tasks.withType(GenerateSwaggerUI::class) {
    dependsOn("openapi3")
}

tasks.register("generateApiSwaggerUI", Copy::class) {
    dependsOn("generateSwaggerUI")
    val generateSwaggerUISampleTask = tasks.named("generateSwaggerUIApi", GenerateSwaggerUI::class).get()
    from(generateSwaggerUISampleTask.outputDir)
    into("$projectDir/src/main/resources/static/docs/swagger-ui")
}

val imageName = project.hasProperty("imageName").let {
    if (it) {
        project.property("imageName") as String
    } else {
        "api"
    }
}
val releaseVersion = project.hasProperty("releaseVersion").let {
    if (it) {
        project.property("releaseVersion") as String
    } else {
        "latest"
    }
}

tasks.register("buildDockerImage") {
    dependsOn("bootJar")
    dependsOn("generateApiSwaggerUI")

    doLast {
        exec {
            workingDir(".")
            commandLine("docker", "run", "--privileged", "--rm", "tonistiigi/binfmt", "--install", "all")
        }

        exec {
            workingDir(".")
            commandLine("docker", "buildx", "create", "--use")
        }

        exec {
            workingDir(".")
            commandLine("docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t", "fewletter/$imageName", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push")
        }
    }
}