tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j")
}

