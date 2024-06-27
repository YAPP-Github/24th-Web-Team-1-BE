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
}