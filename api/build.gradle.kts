import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI

dependencies {
    /** module */
    implementation(project(":api-repo"))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    /** swagger & restdocs */
    implementation("org.springdoc:springdoc-openapi-ui:${DependencyVersion.SPRINGDOC}")
    implementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    implementation("com.epages:restdocs-api-spec-mockmvc:${DependencyVersion.EPAGES_REST_DOCS_API_SPEC}")
}

plugins {
    id("org.asciidoctor.jvm.convert") version DependencyVersion.ASCIIDOCTOR
    id("com.epages.restdocs-api-spec") version DependencyVersion.EPAGES_REST_DOCS_API_SPEC
    id("org.hidetake.swagger.generator") version DependencyVersion.SWAGGER_GENERATOR
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