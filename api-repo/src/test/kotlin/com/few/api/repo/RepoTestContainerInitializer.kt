package com.few.api.repo

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class RepoTestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val log: org.slf4j.Logger =
        org.slf4j.LoggerFactory.getLogger(RepoTestContainerInitializer::class.java)

    companion object {
        private const val MYSQL = "mysql"
        private const val MYSQL_PORT = 3306

        private val dockerCompose =
            DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
                .withExposedService(MYSQL, MYSQL_PORT)
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        log.debug("===== set up test containers =====")

        dockerCompose.start()

        log.debug("===== success set up test containers =====")
    }
}