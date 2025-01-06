tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

plugins {
    id("com.vaadin") version DependencyVersion.VAADIN
}

vaadin {
    pnpmEnable = true
    productionMode = true
}

val production by tasks.creating {
    group = "build"
    description = "Builds the project in production mode."
    dependsOn("build")
    doLast {
        println("Production build completed")
    }
}

tasks {
    bootRun {
        description = "Runs the Spring Boot application"
        group = "application"
    }

    register("prepareFrontend") {
        group = "vaadin"
        description = "Prepare frontend resources"
        dependsOn("vaadinPrepareFrontend")
    }
}
dependencies {
    implementation(project(":library:web"))
    implementation(project(":library:email"))
    implementation(project(":library:event"))

    /** jpa */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    /** aws - sqs */
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:${DependencyVersion.AWS_SQS}")

    /** vaadin */
    api("com.vaadin:vaadin-spring-boot-starter")
}