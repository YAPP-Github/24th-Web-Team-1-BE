tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** module */
    implementation(project(":email"))

    /** spring starter */
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j")

    /** jooq */
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-meta:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-codegen:${DependencyVersion.JOOQ}")
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