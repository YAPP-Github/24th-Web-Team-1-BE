
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.Random

tasks.withType(BootJar::class.java) {
    loaderImplementation = org.springframework.boot.loader.tools.LoaderImplementation.CLASSIC
}

dependencies {
    /** module */
    implementation(project(":repo"))
    implementation(project(":email"))
    implementation(project(":storage"))
    implementation(project(":web"))
    testImplementation(testFixtures(project(":web")))

    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    /** jooq */
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")

    /** Local Cache **/
    implementation("org.ehcache:ehcache:${DependencyVersion.EHCACHE}")

    /** aspectj */
    implementation("org.aspectj:aspectjweaver:${DependencyVersion.ASPECTJ}")

    /** scrimage */
    implementation("com.sksamuel.scrimage:scrimage-core:${DependencyVersion.SCRIMAGE}")
    /** for convert to webp */
    implementation("com.sksamuel.scrimage:scrimage-webp:${DependencyVersion.SCRIMAGE}")

    /** commonmark - markdown to html */
    implementation("org.commonmark:commonmark:${DependencyVersion.COMMONMARK}")

    /** jsoup - html parser */
    implementation("org.jsoup:jsoup:1.15.3")
}

tasks.named("generateStaticSwaggerUI") {
    doLast {
        val swaggerSpecSource = "$projectDir/src/main/resources/static/docs/${project.name}/swagger-ui/swagger-spec.js"

        file(swaggerSpecSource).writeText(
            file(swaggerSpecSource).readText().replace(
                "operationId\" : \"PutImageApi\",",
                "operationId\" : \"PutImageApi\",\n" +
                    putImageRequestScriptSource
            )
        )

        file(swaggerSpecSource).writeText(
            file(swaggerSpecSource).readText().replace(
                "operationId\" : \"ConvertContentApi\",",
                "operationId\" : \"ConvertContentApi\",\n" +
                    putContentRequestScriptSource
            )
        )
    }
}

val putImageRequestScriptSource = "" +
    "        \"requestBody\" : {\n" +
    "            \"content\" : {\n" +
    "                \"multipart/form-data\" : {\n" +
    "                    \"schema\" : {\n" +
    "                        \"type\" : \"object\",\n" +
    "                        \"properties\" : {\n" +
    "                            \"source\" : {\n" +
    "                                \"type\" : \"string\",\n" +
    "                                \"format\" : \"binary\"\n" +
    "                            }\n" +
    "                        }\n" +
    "\n" +
    "                    }\n" +
    "                }\n" +
    "            }\n" +
    "        },"

val putContentRequestScriptSource = "" +
    "        \"requestBody\" : {\n" +
    "            \"content\" : {\n" +
    "                \"multipart/form-data\" : {\n" +
    "                    \"schema\" : {\n" +
    "                        \"type\" : \"object\",\n" +
    "                        \"properties\" : {\n" +
    "                            \"content\" : {\n" +
    "                                \"type\" : \"string\",\n" +
    "                                \"format\" : \"binary\"\n" +
    "                            }\n" +
    "                        }\n" +
    "\n" +
    "                    }\n" +
    "                }\n" +
    "            }\n" +
    "        },"

val imageName = project.hasProperty("imageName").let {
    if (it) {
        project.property("imageName") as String
    } else {
        "fewletter/api"
    }
}
val releaseVersion = project.hasProperty("releaseVersion").let {
    if (it) {
        project.property("releaseVersion") as String
    } else {
        Random().nextInt(90000) + 10000
    }
}

tasks.register("buildDockerImage") {
    dependsOn("bootJar")

    doLast {
        exec {
            workingDir(".")
            commandLine("docker", "run", "--privileged", "--rm", "tonistiigi/binfmt", "--install", "all")
        }

        exec {
            workingDir(".")
            commandLine("docker", "buildx", "create", "--use")
        }

        exec {
            workingDir(".")
            commandLine(
                "docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t",
                "$imageName:latest", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push"
            )
        }

        exec {
            workingDir(".")
            commandLine(
                "docker", "buildx", "build", "--platform=linux/amd64,linux/arm64", "-t",
                "$imageName:$releaseVersion", "--build-arg", "RELEASE_VERSION=$releaseVersion", ".", "--push"
            )
        }
    }
}

tasks.register("buildEcsDockerImage") {
    dependsOn("bootJar")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                '.'
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImageDev") {
    dependsOn("bootJar")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                "-f",
                "Dockerfile.dev.pinpoint",
                '.'
            )
        }
    }
}

tasks.register("buildPinpointEcsDockerImagePrd") {
    dependsOn("bootJar")

    doLast {
        exec {
            workingDir(".")
            commandLine(
                "docker",
                "build",
                "-t",
                imageName,
                "--build-arg",
                "RELEASE_VERSION=$releaseVersion",
                "-f",
                "Dockerfile.prd.pinpoint",
                '.'
            )
        }
    }
}