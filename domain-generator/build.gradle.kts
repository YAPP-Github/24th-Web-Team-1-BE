tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    implementation(project(":web"))

    /** jsoup - html parser */
    implementation("org.jsoup:jsoup:1.15.3")

    /** JSON <-> Class serializer **/
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") //TODO: DB저장으로 로직 변경 후 삭제
}