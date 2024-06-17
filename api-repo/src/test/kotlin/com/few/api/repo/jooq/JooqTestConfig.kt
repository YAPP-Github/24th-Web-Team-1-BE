package com.few.api.repo.jooq

import com.few.api.repo.config.ApiRepoConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = [ApiRepoConfig.BASE_PACKAGE])
class JooqTestConfig