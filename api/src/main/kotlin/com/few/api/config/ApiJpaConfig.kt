package com.few.api.config

import org.springframework.context.annotation.Configuration
import repo.jpa.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = [ApiConfig.BASE_PACKAGE])
class ApiJpaConfig