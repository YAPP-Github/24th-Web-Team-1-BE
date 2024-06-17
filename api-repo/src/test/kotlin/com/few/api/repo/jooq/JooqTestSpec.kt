package com.few.api.repo.jooq

import com.few.api.repo.RepoTestContainerInitializer
import com.few.api.repo.config.ApiRepoConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("new", "test", "api-repo-local")
@SpringBootTest(classes = [ApiRepoConfig::class])
@ContextConfiguration(initializers = [RepoTestContainerInitializer::class])
abstract class JooqTestSpec