
import com.epages.restdocs.apispec.gradle.OpenApi3Task
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.FileWriter
import java.io.InputStream
import java.util.*


tasks.withType(BootJar::class.java) {
    loaderImplementation = org.springframework.boot.loader.tools.LoaderImplementation.CLASSIC
}

plugins {
    /** jooq */
    id("org.jooq.jooq-codegen-gradle") version DependencyVersion.JOOQ
}

dependencies {
    /** domain */
    implementation(project(":domain:crm"))

    /** module */
    implementation(project(":library:email"))
    implementation(project(":library:storage"))
    implementation(project(":library:web"))
    testImplementation(testFixtures(project(":library:web")))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    /** Local Cache **/
    implementation("org.ehcache:ehcache:${DependencyVersion.EHCACHE}")

    /** aspectj */
    implementation("org.aspectj:aspectjweaver:${DependencyVersion.ASPECTJ}")

    /** scrimage */
    implementation("com.sksamuel.scrimage:scrimage-core:${DependencyVersion.SCRIMAGE}")
    /** for convert to webp */
    implementation("com.sksamuel.scrimage:scrimage-webp:${DependencyVersion.SCRIMAGE}")

    /** commonmark - markdown to html */
    implementation("org.commonmark:commonmark:${DependencyVersion.COMMONMARK}")

    /** jsoup - html parser */
    implementation("org.jsoup:jsoup:1.15.3")

    /** mysql */
    implementation("com.mysql:mysql-connector-j")

    /** jooq */
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")
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

tasks.withType(OpenApi3Task::class.java) {
    val multipartformdataPaths =
        listOf(
            "/api/v1/admin/utilities/conversion/image",
            "/api/v1/admin/utilities/conversion/content",
        )
    doLast {
        val input: InputStream = FileInputStream(File("$projectDir/src/main/resources/static/openapi3.yaml"))
        val options =
            DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                isPrettyFlow = true
            }
        val yaml = Yaml(options)
        val yamlData = yaml.loadAll(input)
        for (data in yamlData) {
            val content = data as MutableMap<*, *>
            val paths = content["paths"] as MutableMap<*, *>
            paths.forEach { (path, _methods) ->
                val methods = _methods as MutableMap<*, *>
                // Add security to paths that require Authorization header
                methods.forEach { (_, _details) ->
                    val details = _details as MutableMap<String, Any>
                    if (details.containsKey("parameters")) {
                        val parameters = details["parameters"] as List<Map<String, Any>>
                        parameters.forEach { param ->
                            if (param["name"] == "Authorization") {
                                details["security"] = listOf(mapOf("bearerAuth" to emptyList<String>()))
                            }
                        }
                    }
                }

                // Add requestBody for multipart/form-data paths
                if (multipartformdataPaths.contains(path)) {
                    if (methods.containsKey("post")) {
                        val post = methods["post"] as MutableMap<String, Any>
                        if (!post.containsKey("requestBody")) {
                            post["requestBody"] =
                                mutableMapOf(
                                    "content" to
                                        mutableMapOf(
                                            "multipart/form-data" to
                                                mutableMapOf(
                                                    "schema" to
                                                        mutableMapOf(
                                                            "type" to "object",
                                                            "properties" to
                                                                mutableMapOf(
                                                                    "source" to
                                                                        mutableMapOf(
                                                                            "type" to "string",
                                                                            "format" to "binary",
                                                                        ),
                                                                ),
                                                        ),
                                                ),
                                        ),
                                )
                        }
                    }
                }
            }
            val components = content["components"] as MutableMap<String, MutableMap<String, Any>>
            components["securitySchemes"] =
                mutableMapOf(
                    "bearerAuth" to
                        mutableMapOf(
                            "type" to "http",
                            "scheme" to "bearer",
                            "bearerFormat" to "JWT",
                        ),
                )
            val output = File("$projectDir/src/main/resources/static/openapi3.yaml")
            yaml.dump(content, FileWriter(output))
        }
    }
}

val imageName =
    project.hasProperty("imageName").let {
        if (it) {
            project.property("imageName") as String
        } else {
            "fewletter/api"
        }
    }
val releaseVersion =
    project.hasProperty("releaseVersion").let {
        if (it) {
            project.property("releaseVersion") as String
        } else {
            Random().nextInt(90000) + 10000
        }
    }

tasks.register("buildDockerImage") {
    dependsOn("build")

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
            commandLine(
                "docker",
                "buildx",
                "build",
                "--platform=linux/amd64,linux/arm64",
                "-t",
                "$imageName:latest",
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                ".",
                "--push",
            )
        }

        exec {
            workingDir(".")
            commandLine(
                "docker",
                "buildx",
                "build",
                "--platform=linux/amd64,linux/arm64",
                "-t",
                "$imageName:$releaseVersion",
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                ".",
                "--push",
            )
        }
    }
}

tasks.register("buildEcsDockerImage") {
    dependsOn("build")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                '.',
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImageDev") {
    dependsOn("build")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                "-f",
                "Dockerfile.dev.pinpoint",
                '.',
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImagePrd") {
    dependsOn("build")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                "-f",
                "Dockerfile.prd.pinpoint",
                '.',
            )
        }
    }
}