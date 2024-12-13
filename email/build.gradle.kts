tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** starter */
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    /** aws ses */
    implementation("com.amazonaws:aws-java-sdk-ses:${DependencyVersion.AWS_SES}")
}