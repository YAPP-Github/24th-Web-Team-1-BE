tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** aws - s3 */
    implementation("com.amazonaws:aws-java-sdk-s3:${DependencyVersion.AWS_SDK}")
}