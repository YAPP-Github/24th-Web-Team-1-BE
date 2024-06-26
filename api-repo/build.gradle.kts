tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** module */
    api(project(":data"))

    /** spring starter */
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")
}