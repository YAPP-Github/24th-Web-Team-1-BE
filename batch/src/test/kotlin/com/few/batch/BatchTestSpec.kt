package com.few.batch

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.batch.config.BatchConfig
import com.few.email.service.article.SendArticleEmailService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("new", "test")
@SpringBootTest(classes = [BatchConfig::class, ObjectMapper::class])
@ContextConfiguration(initializers = [BatchTestContainerInitializer::class])
abstract class BatchTestSpec {

    @MockBean
    lateinit var sendArticleEmailService: SendArticleEmailService
}