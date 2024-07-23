tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** module */
    api(project(":data"))

    /** mysql */
    implementation("com.mysql:mysql-connector-j")

    /** jooq */
    api("org.springframework.boot:spring-boot-starter-jooq")
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")

    /** Local Cache **/
    implementation("org.ehcache:ehcache:${DependencyVersion.EHCACHE}")
    implementation("org.springframework.boot:spring-boot-starter-cache")
}