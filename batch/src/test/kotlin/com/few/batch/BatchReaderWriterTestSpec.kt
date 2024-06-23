package com.few.batch

import com.few.batch.config.BatchConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("new", "test", "batch-test", "email-local")
@SpringBootTest(classes = [BatchConfig::class])
@ContextConfiguration(initializers = [BatchTestContainerInitializer::class])
abstract class BatchReaderWriterTestSpec