tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** aws - s3 */
    implementation("com.amazonaws:aws-java-sdk-s3:${DependencyVersion.AWS_SDK}")

    /** commonmark - markdown to html */
    implementation("org.commonmark:commonmark:${DependencyVersion.COMMONMARK}")

    /** jsoup - html parser */
    implementation("org.jsoup:jsoup:1.15.3")
}