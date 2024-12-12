tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    api(project(":security"))

    /** spring starter */
    api("org.springframework.boot:spring-boot-starter-web")

    /** swagger & restdocs */
    api("org.springdoc:springdoc-openapi-ui:${DependencyVersion.SPRINGDOC}")
    api("org.springframework.restdocs:spring-restdocs-webtestclient")
    api("org.springframework.restdocs:spring-restdocs-mockmvc")
    api("com.epages:restdocs-api-spec-mockmvc:${DependencyVersion.EPAGES_REST_DOCS_API_SPEC}")
}