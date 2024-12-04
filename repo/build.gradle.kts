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
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

    /** Local Cache **/
    api("org.ehcache:ehcache:${DependencyVersion.EHCACHE}")
    api("org.springframework.boot:spring-boot-starter-cache")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")
}