package com.few.api.web.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class ControllerTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val log = KotlinLogging.logger {}

    companion object {
        private const val MYSQL = "mysql"
        private const val MYSQL_PORT = 3306

        private val dockerCompose =
            DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
                .withExposedService(MYSQL, MYSQL_PORT)
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        log.debug { "===== set up test containers =====" }

        dockerCompose.start()

        log.debug { "===== success set up test containers =====" }
    }
}