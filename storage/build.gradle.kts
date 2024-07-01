tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    // minio - local
    implementation("io.minio:minio:${DependencyVersion.MINIO}")
    // s3
    implementation("com.amazonaws:aws-java-sdk-s3:${DependencyVersion.AWS_SDK}")

    // commonmark - markdown to html
    implementation("org.commonmark:commonmark:${DependencyVersion.COMMONMARK}")

    // jsoup
    implementation("org.jsoup:jsoup:1.15.3")
}