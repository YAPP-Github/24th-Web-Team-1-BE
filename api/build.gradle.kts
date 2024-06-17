dependencies {
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