tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** module */
    implementation(project(":data"))

    /** mysql */
    implementation("com.mysql:mysql-connector-j")

    /** jooq */
    api("org.springframework.boot:spring-boot-starter-jooq")

    /** jpa */
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")
}