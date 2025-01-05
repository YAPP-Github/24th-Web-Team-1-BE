package com.few.crm.email.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.crm.email.domain.EmailSendEventType
import com.few.crm.email.event.send.EmailSentEvent
import com.few.crm.email.repository.EmailTemplateHistoryRepository
import com.few.crm.email.repository.EmailTemplateRepository
import com.few.crm.email.service.CrmSendNonVariablesEmailService
import com.few.crm.email.service.NonContent
import com.few.crm.email.service.SendEmailArgs
import com.few.crm.email.usecase.dto.SendNotificationEmailUseCaseIn
import com.few.crm.email.usecase.dto.SendNotificationEmailUseCaseOut
import com.few.crm.user.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

data class NotificationEmailTemplateProperties(
    val subject: String,
    val body: String,
)

@Service
class SendNotificationEmailUseCase(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailTemplateHistoryRepository: EmailTemplateHistoryRepository,
    private val userRepository: UserRepository,
    private val crmSendNonVariablesEmailService: CrmSendNonVariablesEmailService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val objectMapper: ObjectMapper,
) {
    fun execute(useCaseIn: SendNotificationEmailUseCaseIn): SendNotificationEmailUseCaseOut {
        val templateId = useCaseIn.templateId
        val templateVersion: Float? = useCaseIn.templateVersion
        val userIds = useCaseIn.userIds
        val sendType = "email"

        val properties =
            templateVersion?.let { it ->
                emailTemplateHistoryRepository
                    .findByTemplateIdAndVersion(templateId, it)
                    ?.let {
                        NotificationEmailTemplateProperties(
                            subject = it.subject,
                            body = it.body,
                        )
                    }
                    ?: throw IllegalArgumentException("Template not found")
            } ?: emailTemplateRepository
                .findById(templateId)
                .orElseThrow { IllegalArgumentException("Template not found") }
                .let {
                    NotificationEmailTemplateProperties(
                        subject = it.subject,
                        body = it.body,
                    )
                }

        val targetUsers =
            if (userIds.isEmpty()) {
                userRepository
                    .findAllExistByUserAttributesKey()
                    .groupBy {
                        objectMapper.readValue(it.userAttributes, Map::class.java)[sendType] as String
                    }
            } else {
                userRepository
                    .findAllByIdIn(userIds)
                    .filter {
                        objectMapper.readValue(it.userAttributes, Map::class.java)[sendType] != null
                    }.groupBy {
                        objectMapper.readValue(it.userAttributes, Map::class.java)[sendType] as String
                    }
            }

        targetUsers.keys.forEach { email ->
            val emailMessageId =
                crmSendNonVariablesEmailService.send(
                    SendEmailArgs(
                        to = email,
                        subject = properties.subject,
                        template = properties.body,
                        content = NonContent(),
                    ),
                )

            applicationEventPublisher.publishEvent(
                EmailSentEvent(
                    userExternalId = targetUsers[email]!!.first().externalId!!,
                    emailBody = properties.body,
                    destination = email,
                    messageId = emailMessageId,
                    eventType = EmailSendEventType.SEND.name,
                ),
            )
        }

        return SendNotificationEmailUseCaseOut(
            isSuccess = true,
        )
    }
}