tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** module */
    implementation(project(":email"))

    /** mysql */
    implementation("com.mysql:mysql-connector-j")

    /** jooq */
    api("org.springframework.boot:spring-boot-starter-jooq")
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")

    /** aspectj */
    implementation("org.aspectj:aspectjweaver")

    /** test flyway */
    testImplementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    testImplementation("org.flywaydb:flyway-mysql")
}