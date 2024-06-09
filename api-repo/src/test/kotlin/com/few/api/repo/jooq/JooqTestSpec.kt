package com.few.api.repo.jooq

import com.few.api.repo.config.ApiRepoConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("new", "test", "api-repo-local")
@SpringBootTest(classes = [ApiRepoConfig::class])
abstract class JooqTestSpec
