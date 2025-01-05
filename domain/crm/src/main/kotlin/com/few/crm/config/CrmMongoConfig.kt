package com.few.crm.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
    basePackages = [CrmConfig.BASE_PACKAGE],
)
class CrmMongoConfig