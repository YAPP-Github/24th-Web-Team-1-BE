package com.few.crm.email.usecase

import com.few.crm.email.domain.EmailTemplate
import com.few.crm.email.domain.EmailTemplateHistory
import com.few.crm.email.repository.EmailTemplateHistoryRepository
import com.few.crm.email.repository.EmailTemplateRepository
import com.few.crm.email.usecase.dto.PostTemplateUseCaseIn
import com.few.crm.email.usecase.dto.PostTemplateUseCaseOut
import org.springframework.stereotype.Service

@Service
class PostTemplateUseCase(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailTemplateHistoryRepository: EmailTemplateHistoryRepository,
) {
    fun execute(useCaseIn: PostTemplateUseCaseIn): PostTemplateUseCaseOut {
        val id: Long? = useCaseIn.id
        val templateName = useCaseIn.templateName
        val subject: String? = useCaseIn.subject
        val version: Float? = useCaseIn.version
        // TODO: body & variables 유효성 검사
        val body = useCaseIn.body
        val variables = useCaseIn.variables

        val persistedTemplate: EmailTemplate? =
            id?.let {
                emailTemplateRepository
                    .findById(it)
                    .orElseThrow { IllegalArgumentException("Template not found") }
            } ?: emailTemplateRepository
                .findByTemplateName(templateName)
                ?.let {
                    throw IllegalArgumentException("Duplicate template name: $templateName")
                }

        return (
            persistedTemplate
                ?.modifySubject(subject)
                ?.modifyBody(body, variables)
                ?: run {
                    EmailTemplate.new(
                        templateName = templateName,
                        subject = subject!!,
                        body = body,
                        variables = variables,
                    )
                }
        ).let { template ->
            if (template.isNewTemplate()) {
                emailTemplateRepository.save(template)
            } else {
                template.updateVersion(version).let {
                    emailTemplateRepository.save(it)
                }
            }
            // TODO: Refactor to event
        }.let { savedTemplate ->
            emailTemplateHistoryRepository.save(
                EmailTemplateHistory(
                    templateId = savedTemplate.id!!,
                    subject = savedTemplate.subject,
                    body = savedTemplate.body,
                    variables = savedTemplate.variables,
                    version = savedTemplate.version,
                ),
            )
        }.let { template ->
            PostTemplateUseCaseOut(
                id = template.templateId, // TODO Fix to template.id
                templateName = templateName, // TODO Fix to template.templateName
                version = template.version,
            )
        }
    }
}