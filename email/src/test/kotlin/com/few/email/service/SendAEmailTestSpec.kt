package com.few.email.service

import com.few.email.config.MailConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test", "email-test")
@SpringBootTest(classes = [MailConfig::class])
abstract class SendAEmailTestSpec