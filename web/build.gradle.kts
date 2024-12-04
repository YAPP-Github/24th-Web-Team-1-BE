tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    api(project(":security"))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-web")
}