tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    implementation(project(":library:web"))
    implementation(project(":library:email"))
    implementation(project(":library:event"))

    /** jpa */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    /** aws - sqs */
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:${DependencyVersion.AWS_SQS}")
}