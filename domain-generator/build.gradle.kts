tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

dependencies {
    implementation(project(":web"))

    /** jsoup - html parser */
    implementation("org.jsoup:jsoup:1.15.3")

    /** coroutines **/
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-spring:1.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    /** HTTP client **/
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    /** gson **/
    implementation("com.google.code.gson:gson:2.10.1")

    /** JSON <-> Class serializer **/
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") //TODO: DB저장으로 로직 변경 후 삭제
}