
import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.Random

tasks.withType(BootJar::class.java) {
    loaderImplementation = org.springframework.boot.loader.tools.LoaderImplementation.CLASSIC
}

dependencies {
    /** module */
    implementation(project(":api-repo"))
    implementation(project(":batch"))
    implementation(project(":email"))
    implementation(project(":storage"))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    /** jwt */
    implementation("io.jsonwebtoken:jjwt-api:${DependencyVersion.JWT}")
    implementation("io.jsonwebtoken:jjwt-impl:${DependencyVersion.JWT}")
    implementation("io.jsonwebtoken:jjwt-jackson:${DependencyVersion.JWT}")

    /** aspectj */
    implementation("org.aspectj:aspectjweaver:1.9.5")

    /** scrimage */
    implementation("com.sksamuel.scrimage:scrimage-core:${DependencyVersion.SCRIMAGE}")
    /** for convert to webp */
    implementation("com.sksamuel.scrimage:scrimage-webp:${DependencyVersion.SCRIMAGE}")

    /** swagger & restdocs */
    implementation("org.springdoc:springdoc-openapi-ui:${DependencyVersion.SPRINGDOC}")
    implementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    implementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    implementation("com.epages:restdocs-api-spec-mockmvc:${DependencyVersion.EPAGES_REST_DOCS_API_SPEC}")
    swaggerUI("org.webjars:swagger-ui:${DependencyVersion.SWAGGER_UI}")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:mysql")
}

plugins {
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR
}

val serverUrl = project.hasProperty("serverUrl").let {
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
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "openapi3"
}

/** convert snippet to postman */
postman {
    title = project.name
    version = project.version.toString()
    baseUrl = serverUrl
    outputDirectory = "src/main/resources/static/"
    outputFileNamePrefix = "postman"
}

/** generate swagger ui */
swaggerSources {
    /** generateSwaggerUIApi */
    register("api") {
        setInputFile(file("$projectDir/src/main/resources/static/openapi3.yaml"))
    }
}

/**
 * generate static swagger ui <br/>
 * need snippet to generate swagger ui
 * */
tasks.register("generateStaticSwaggerUIApi", Copy::class) {
    /** generateSwaggerUIApi */
    dependsOn("generateSwaggerUIApi")
    val generateSwaggerUISampleTask = tasks.named("generateSwaggerUIApi", GenerateSwaggerUI::class).get()

    /** copy */
    from(generateSwaggerUISampleTask.outputDir)
    into("$projectDir/src/main/resources/static/docs/swagger-ui")
    doLast {
        val swaggerSpecSource = "$projectDir/src/main/resources/static/docs/swagger-ui/swagger-spec.js"
        file(swaggerSpecSource).writeText(
            file(swaggerSpecSource).readText().replace(
                "operationId\" : \"PutImageApi\",",
                "operationId\" : \"PutImageApi\",\n" +
                    putImageRequestScriptSource
            )
        )

        file(swaggerSpecSource).writeText(
            file(swaggerSpecSource).readText().replace(
                "operationId\" : \"ConvertContentApi\",",
                "operationId\" : \"ConvertContentApi\",\n" +
                    putContentRequestScriptSource
            )
        )
    }
}

val putImageRequestScriptSource = "" +
    "        \"requestBody\" : {\n" +
    "            \"content\" : {\n" +
    "                \"multipart/form-data\" : {\n" +
    "                    \"schema\" : {\n" +
    "                        \"type\" : \"object\",\n" +
    "                        \"properties\" : {\n" +
    "                            \"source\" : {\n" +
    "                                \"type\" : \"string\",\n" +
    "                                \"format\" : \"binary\"\n" +
    "                            }\n" +
    "                        }\n" +
    "\n" +
    "                    }\n" +
    "                }\n" +
    "            }\n" +
    "        },"

val putContentRequestScriptSource = "" +
    "        \"requestBody\" : {\n" +
    "            \"content\" : {\n" +
    "                \"multipart/form-data\" : {\n" +
    "                    \"schema\" : {\n" +
    "                        \"type\" : \"object\",\n" +
    "                        \"properties\" : {\n" +
    "                            \"content\" : {\n" +
    "                                \"type\" : \"string\",\n" +
    "                                \"format\" : \"binary\"\n" +
    "                            }\n" +
    "                        }\n" +
    "\n" +
    "                    }\n" +
    "                }\n" +
    "            }\n" +
    "        },"

val imageName = project.hasProperty("imageName").let {
    if (it) {
        project.property("imageName") as String
    } else {
        "fewletter/api"
    }
}
val releaseVersion = project.hasProperty("releaseVersion").let {
    if (it) {
        project.property("releaseVersion") as String
    } else {
        Random().nextInt(90000) + 10000
    }
}

tasks.register("buildDockerImage") {
    dependsOn("bootJar")

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
                "docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t",
                "$imageName:latest", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push"
            )
        }

        exec {
            workingDir(".")
            commandLine(
                "docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t",
                "$imageName:$releaseVersion", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push"
            )
        }
    }
}

tasks.register("buildEcsDockerImage") {
    dependsOn("bootJar")

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
                '.'
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImageDev") {
    dependsOn("bootJar")

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
                '.'
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImagePrd") {
    dependsOn("bootJar")

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
                '.'
            )
        }
    }
}